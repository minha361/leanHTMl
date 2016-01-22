USE [Adayroi_Dichvu]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		NoiNd
-- Create date: 13-10-2015
-- =============================================
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Get_All_Cat]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[Get_All_Cat]
GO
CREATE PROCEDURE [dbo].[Get_All_Cat]
AS
BEGIN
	SET NOCOUNT ON;
	SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
	WITH CAT_1 AS
	(
	  SELECT CateID,
			 CAST(CateID AS NVARCHAR) + '_' + CAST(Visible AS NVARCHAR) + '_' + CAST(Sort AS NVARCHAR) + '_' + CAST(CateName AS NVARCHAR) AS CatInfo,
			 Visible
	  FROM Category
	  WHERE CateParentID = CateID
	),
	CAT_2 AS
	(
	  SELECT C.CateID,
			 CAST(C.CateID AS NVARCHAR) + '_' + CAST((C.Visible & CAT_1.Visible) AS NVARCHAR) + '_' + CAST(C.Sort AS NVARCHAR) + '_' + CAST(C.CateName AS NVARCHAR) AS CatInfo,
			 (C.Visible & CAT_1.Visible) AS Visible
	  FROM Category C
		INNER JOIN CAT_1 ON C.CateParentID = CAT_1.CateId
	),
	CAT_3 AS
	(
	  SELECT C.CateID,
			 CAST(C.CateID AS NVARCHAR) + '_' + CAST((C.Visible & CAT_2.Visible) AS NVARCHAR) + '_' + CAST(C.Sort AS NVARCHAR) + '_' + CAST(C.CateName AS NVARCHAR) AS CatInfo,
			 (C.Visible & CAT_2.Visible) AS Visible
	  FROM Category C
		INNER JOIN CAT_2 ON C.CateParentID = CAT_2.CateId
	),
	CAT_TOTAL AS
	(
	  SELECT CateID,
			 REVERSE(Parsename (REVERSE (CatePath),1)) Cate1Id,
			 REVERSE(Parsename (REVERSE (CatePath),2)) Cate2Id,
			 REVERSE(Parsename (REVERSE (CatePath),3)) Cate3Id,
			 CateName,
			 CatePath,
			 Sort,
			 Visible
	  FROM Category
	)
	SELECT CAT_TOTAL.CateID,
		   CAT_TOTAL.CateName,
		   CAT_TOTAL.CatePath,
		   CAT_TOTAL.Sort,
		   (CAT_1.Visible & ISNULL(CAT_2.Visible,1) & ISNULL(CAT_3.Visible,1) & CAT_TOTAL.Visible) AS Visible,
		   CAT_1.CatInfo AS Cat1Info,
		   CAT_2.CatInfo AS Cat2Info,
		   CAT_3.CatInfo AS Cat3Info
	FROM CAT_TOTAL
	  LEFT JOIN CAT_1 ON CAT_TOTAL.Cate1Id = CAT_1.CateID
	  LEFT JOIN CAT_2 ON CAT_TOTAL.Cate2Id = CAT_2.CateID
	  LEFT JOIN CAT_3 ON CAT_TOTAL.Cate3Id = CAT_3.CateID
	SET NOCOUNT OFF;
END
GO