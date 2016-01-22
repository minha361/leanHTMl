USE [Adayroi_CategoryManagement]
GO


SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Product_Solr_Get_All_Attribute]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[Product_Solr_Get_All_Attribute]
GO
CREATE PROCEDURE [dbo].[Product_Solr_Get_All_Attribute]
AS
BEGIN
	SET NOCOUNT ON;
	SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
    SELECT ProductItemId,
		   AttributeId,
		   Value,
		   AttributeValueId,
		   AttributeName
	FROM (SELECT PAM.ProductItemId,
				 PAM.AttributeId,
				 AV.Value,
				 PAM.AttributeValueId,
				 A.AttributeName,
				 ROW_NUMBER() OVER (PARTITION BY PAM.ProductItemId,PAM.AttributeId ORDER BY AV.Value DESC) AS num
		  FROM Product_Attribute_Mapping AS PAM
			INNER JOIN Attribute AS A ON A.Id = PAM.AttributeId
			INNER JOIN AttributeValue AS AV ON AV.Id = PAM.AttributeValueId
		  WHERE A.AttributeStatus = 1
		  AND   AV.AttributeValueStatus = 1) AS Temp
	WHERE num = 1

	SET NOCOUNT OFF;
END

GO

