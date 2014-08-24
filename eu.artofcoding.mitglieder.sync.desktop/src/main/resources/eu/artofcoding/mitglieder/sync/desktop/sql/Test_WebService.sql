UPDATE [BVK$Contact]
SET [Address 2] = 'Waldlehne 115'
WHERE [No_] = '015004'
GO
INSERT INTO [BVK$MyBVK Kontakte] ([Contact No_], [End Membership], [Status]) VALUES ('015004', '', 2)
GO
SELECT
  *
FROM [BVK$MyBVK Kontakte]
GO
SELECT
  *
FROM bvkplatform_contact_view
GO
