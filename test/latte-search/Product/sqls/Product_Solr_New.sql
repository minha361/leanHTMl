USE [Adayroi_CategoryManagement]
GO
/****** Object:  StoredProcedure [dbo].[Product_Solr_New]    Script Date: 6/17/2015 9:49:16 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Product_Solr_New]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[Product_Solr_New]
GO
CREATE PROCEDURE [dbo].[Product_Solr_New]
AS
BEGIN
SET NOCOUNT ON;
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
WITH PO AS
(
  SELECT *
  FROM (SELECT *,
               ROW_NUMBER() OVER (PARTITION BY WarehouseProductItemMappingId ORDER BY PromotionProductItemStatus DESC,PromotionStatus DESC,StartDate ASC) AS num
        FROM (SELECT PPM.WarehouseProductItemMappingId,
                     PPM.PromotionPrice,
                     PRO.StartDate,
                     PRO.FinishDate,
                     CASE
                       WHEN PPM.PromotionProductItemStatus = 1 AND PPM.RemainQuantity > 0 THEN 1
                       ELSE 0
                     END AS PromotionProductItemStatus,
                     CASE
                       WHEN PRO.PromotionStatus = 2 THEN 1
                       ELSE 0
                     END AS PromotionStatus
              FROM Promotion_ProductItem_Mapping AS PPM
                INNER JOIN Promotions AS PRO ON PPM.PromotionId = PRO.Id
              WHERE PRO.FinishDate > getdate ()) AS tmp1) AS tmp2
  WHERE num = 1
),
DATA_P AS
(
  SELECT WH_PI.Id AS ProductItemWarehouseId,
         P.Barcode AS Barcode,
         WH.Id AS WarehouseId,
         P.Id AS ProductId,
         P.CategoryId AS CategoryId,
         P_I.Id AS ProductItemId,
         WH_PI.SellPrice AS SellPrice,
         WH_PI.Quantity AS Quantity,
         BR.BrandName AS BrandName,
         BR.Id AS BrandId,
         P_I.ProductItemName,
         P_I.CreatedDate AS CreateTime,
         WH_PI.MerchantId AS MerchantId,
         MC.MerchantName AS MerchantName,
         WH_PI.MerchantSKU AS MerchantProductItemSKU,
         MC.MerchantStatus AS MerchantStatus,
         P_I.ProductItemStatus AS ProductItemStatus,
         BR.BrandStatus AS BrandStatus,
         WH_PI.MerchantProductItemStatus AS MerchantProductItemStatus,
         CASE
           WHEN PO.WarehouseProductItemMappingId IS NOT NULL AND PO.PromotionProductItemStatus = 1 THEN 1
           ELSE 0
         END AS IsPromotionMapping,
         CASE
           WHEN PO.WarehouseProductItemMappingId IS NOT NULL AND PO.PromotionStatus = 1 THEN 1
           ELSE 0
         END AS IsPromotion,
         PO.PromotionPrice,
         PO.StartDate AS StartDateDiscount,
         PO.FinishDate AS FinishDateDiscount,
         CASE
           WHEN P_I.ProductItemType = 2 OR P_I.ProductItemType = 4 OR P_I.ProductItemPolicy <> 1 THEN CAST(WH.ProvinceId AS VARCHAR) + ',0'
           ELSE '4,8,0'
         END AS CityIds,
         P_I.Weight AS Weight,
         P_I.ProductItemType AS ProductItemType,
         P_I.Images AS Images,
         WH_PI.IsVisible,
         WH_PI.IsVisible AS OnSite,
         WH_PI.VatStatus,
         WH_PI.PriceStatus,
         BS.JsonScore AS Score,
         BS.Score AS BoostScore,
         BS.DistrictsJson AS AdministrativeUnit,
         P_I.ProductItemPolicy,
         P_I.AttributeDetail AS AttributeDetail,
         LPPIM.Priority AS LandingPagePriority,
         LPPIM.LandingPageId,
         LPPIM.LandingPageGroupId,
         CASE
           WHEN WH_PI.OriginalPrice > WH_PI.SellPrice THEN 'true'
           ELSE 'false'
         END AS PriceFlag
  FROM (SELECT Id, BrandId, CategoryId, Barcode, ProductName FROM Product) AS P
    INNER JOIN (SELECT Id,
                       ProductId,
                       ProductItemName,
                       ProductItemStatus,
                       Weight,
                       ProductItemType,
                       Images,
                       CreatedDate,
                       ProductItemPolicy,
                       AttributeDetail
                FROM ProductItem) AS P_I ON P_I.ProductId = P.Id
    INNER JOIN (SELECT Id,
                       ProductItemId,
                       MerchantId,
                       WarehouseId,
                       SellPrice,
                       OriginalPrice,
                       (Quantity - Isnull(SafetyStock,0)) AS Quantity,
                       MerchantProductItemStatus,
                       MerchantSku,
                       SafetyStock,
                       CreatedDate,
                       IsVisible,
                       VatStatus,
                       PriceStatus
                FROM Warehouse_ProductItem_Mapping) AS WH_PI ON WH_PI.ProductItemId = P_I.Id
    INNER JOIN (SELECT Id, MerchantName, MerchantStatus FROM MerchantProfile) AS MC ON MC.Id = WH_PI.MerchantId
    INNER JOIN (SELECT Id, ProvinceId FROM Warehouse) AS WH ON WH.Id = WH_PI.WarehouseId
    INNER JOIN (SELECT Id, BrandName, BrandStatus FROM Brand) AS BR ON BR.Id = P.BrandId
    LEFT JOIN PO ON PO.WarehouseProductItemMappingId = WH_PI.Id
    LEFT JOIN (SELECT WarehouseProductMapId,
                      Score,
                      DistrictsJson,
                      JsonScore
               FROM BootstScore) AS BS ON BS.WarehouseProductMapId = WH_PI.Id
    LEFT JOIN (SELECT WarehouseProductItemId,
                      Priority,
                      LandingPageId,
                      LandingPageGroupId
               FROM LandingPage_ProductItem_Mapping
               WHERE StatusId = 1) AS LPPIM ON LPPIM.WarehouseProductItemId = WH_PI.Id
)
SELECT *
FROM DATA_P
set nocount off;
END