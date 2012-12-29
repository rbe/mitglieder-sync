SET NOCOUNT ON

--
-- Tabelle, die Mitgliedsnr. von geänderten Mitgliedern aufnimmt
--
IF object_id('dbo.bvkplatform_inittable', 'P') IS NOT null
  DROP PROCEDURE dbo.bvkplatform_inittable;
GO
CREATE PROCEDURE dbo.bvkplatform_inittable(
  @force INT = 0
)
AS
  IF @force >= 0 AND object_id('dbo.bvkplatform_contact_trigger', 'TR') IS NOT null
    DROP TRIGGER dbo.bvkplatform_contact_trigger
IF @force >= 0 AND object_id('dbo.bvkplatform_getfunctions', 'FN') IS NOT null
  DROP FUNCTION dbo.bvkplatform_getfunctions
IF @force >= 0 AND object_id('dbo.bvkplatform_getfunctions2', 'FN') IS NOT null
  DROP FUNCTION dbo.bvkplatform_getfunctions2
IF @force >= 0 AND object_id('dbo.bvkplatform_geteducation', 'FN') IS NOT null
  DROP FUNCTION dbo.bvkplatform_geteducation
IF @force >= 0 AND object_id('dbo.bvkplatform_getcompanies', 'FN') IS NOT null
  DROP FUNCTION dbo.bvkplatform_getcompanies
IF @force >= 0 AND object_id('dbo.myisnull', 'FN') IS NOT null
  DROP FUNCTION dbo.myisnull
IF @force >= 0 AND object_id('dbo.bvkplatform_isnull', 'FN') IS NOT null
  DROP FUNCTION dbo.bvkplatform_isnull
IF @force >= 0 AND object_id('dbo.mydate', 'FN') IS NOT null
  DROP FUNCTION dbo.mydate
IF @force >= 0 AND object_id('dbo.bvkplatform_date', 'FN') IS NOT null
  DROP FUNCTION dbo.bvkplatform_date
IF @force >= 0 AND object_id('dbo.bvkplatform_contact_view', 'V') IS NOT null
  DROP VIEW dbo.bvkplatform_contact_view
IF @force >= 0 AND object_id('dbo.bvkplatform_updateall', 'P') IS NOT null
  DROP PROCEDURE dbo.bvkplatform_updateall
IF @force = 2 AND object_id('dbo.bvkplatform_contact', 'U') IS NOT null
  DROP TABLE dbo.bvkplatform_contact
--    if @force = 1 and object_id('dbo.bvkplatform_contact', 'U') is null
--       create table dbo.bvkplatform_contact (
--                [Contact No_] nvarchar(6) COLLATE SQL_Latin1_General_CP850_CS_AS
--                , [Status] int
--                , [End Membership] datetime
--        )
GO

EXEC bvkplatform_inittable @force = 2
GO

--
-- Trigger, der geänderte Mitglieder in bvkplatform_contact schreibt
--
--create trigger dbo.bvkplatform_contact_trigger
--   on dbo.BVK$Conact
--   after insert, update /*, delete*/
--as
--begin
--	declare @exist int;
--	declare @num nvarchar(6);
--	declare @status int;
----        declare @fktmybvk int;
--	declare @endmembership datetime;
--	set nocount on;
--	declare bvkcontact_cur cursor fast_forward read_only for select [No_], [Status], /*[FktMyBVK],*/ [End Membership] = dbo.bvkplatform_date([End Membership]) from inserted;
--	open bvkcontact_cur;
--	fetch next from bvkcontact_cur into @num, @status, /*@fktmybvk,*/ @endmembership;
--	while @@fetch_status = 0
--	begin
--                if @status = 2 /*or @fktmybvk > 0*/
--                begin
--                    select @exist = count(*) from bvkplatform_contact where [Contact No_] = @num
--                    if @exist = 0 and (@endmembership is null or @endmembership >= getdate())
--                    begin
--                            insert into bvkplatform_contact ([Contact No_], [Status], [End Membership]) values (@num, @status, @endmembership);
--                    end
--                    fetch next from bvkcontact_cur into @num, @status, @endmembership;
--                end
--	end
--	close bvkcontact_cur;
--	deallocate bvkcontact_cur;
--end
--go

--
-- Ehrenämter
--
CREATE FUNCTION dbo.bvkplatform_getfunctions(
  @contactno NVARCHAR(6) = null
)
  RETURNS NVARCHAR(1000)
AS
  BEGIN
    IF @contactno IS null
      RETURN N''
    DECLARE @x AS NVARCHAR(1000)
    SET @x = ''
    SELECT
      @x = @x + cast([Beschreibung in MyBVK] AS NVARCHAR(100)) + N','
    FROM dbo.[BVK$Committee Member]
    WHERE [Contact No.] = @contactno
          AND [Active] = 1 -- 1=YES
          AND ltrim(rtrim([Beschreibung in MyBVK])) <> N'' -- and [Beschreibung in MyBVK] <> N' '
          AND [Beschreibung in MyBVK] IS NOT null
    IF len(@x) > 0
      SELECT
        @x = left(@x, len(@x) - 1)
    RETURN @x
  END
GO

--
-- Ehrenämter - 2
--
CREATE FUNCTION dbo.bvkplatform_getfunctions2(
  @contactno NVARCHAR(6) = null
)
  RETURNS NVARCHAR(1000)
AS
  BEGIN
    IF @contactno IS null
      RETURN N''
    DECLARE @x AS NVARCHAR(1000)
    SET @x = ''
    SELECT
      @x = @x + cast([Function Code] AS NVARCHAR(10)) + N','
    FROM dbo.[BVK$Committee Member]
    WHERE [Contact No.] = @contactno
          AND [Active] = 1 -- 1=YES
    IF len(@x) > 0
      SELECT
        @x = left(@x, len(@x) - 1)
    RETURN @x
  END
GO

--
-- Ausbildung
--
CREATE FUNCTION dbo.bvkplatform_geteducation(
  @contactno NVARCHAR(6) = null
)
  RETURNS NVARCHAR(1000)
AS
  BEGIN
    IF @contactno IS null
      RETURN N''
    DECLARE @a1 INT
    DECLARE @a2 INT
    DECLARE @a3 INT
    DECLARE @x  NVARCHAR(1000)
    SET @x = N''
    SELECT
      @a1 = Ausbildung1,
      @a2 = Ausbildung2,
      @a3 = Ausbildung3
    FROM [BVK$Contact]
    WHERE [No.] = @contactno
    SELECT
      @x = replace(replace(
                       cast((SELECT
                               Optionswert
                             FROM [BVK$MyBVK Optionswerte]
                             WHERE Feldname = N'Ausbildung1' AND Integerwert = @a1) AS NVARCHAR)
                       + N', '
                       + cast((SELECT
                               Optionswert
                               FROM [BVK$MyBVK Optionswerte]
                               WHERE Feldname = N'Ausbildung2' AND Integerwert = @a2) AS NVARCHAR)
                       + N', '
                       + cast((SELECT
                               Optionswert
                               FROM [BVK$MyBVK Optionswerte]
                               WHERE Feldname = N'Ausbildung3' AND Integerwert = @a3) AS NVARCHAR)
                       , ', ', ','), ',,', '')
    RETURN @x
  END
GO

--
-- Unternehmen
--
CREATE FUNCTION dbo.bvkplatform_getcompanies(
  @contactno NVARCHAR(6) = null
)
  RETURNS NVARCHAR(1000)
AS
  BEGIN
    IF @contactno IS null
      RETURN N''
    DECLARE @x AS NVARCHAR(1000)
    SET @x = N''
    SELECT
      @x = @x + cast((SELECT
                        [VU/BU-Name]
                      FROM [BVK$Contact]
                      WHERE [No.] = [Relation to No.]) AS NVARCHAR(50)) + N','
    FROM dbo.[BVK$Adressbeziehungen]
    WHERE [Contact No.] = @contactno
    IF len(@x) > 0
      SELECT
        @x = left(@x, len(@x) - 1)
    RETURN @x
  END
GO

--
-- bvkplatform_isnull
--
CREATE FUNCTION dbo.bvkplatform_isnull(
  @value NVARCHAR(255)
)
  RETURNS NVARCHAR(255)
AS
  BEGIN
    IF @value IS null OR ltrim(rtrim(@value)) = ''
      SET @value = null
    RETURN @value
  END
GO

--
-- bvkplatform_date
--
CREATE FUNCTION dbo.bvkplatform_date(
  @value DATETIME
)
  RETURNS DATETIME
AS
  BEGIN
    IF year(@value) = 1753
      SET @value = null
    RETURN @value
  END
GO

--
--
--
CREATE VIEW dbo.bvkplatform_contact_view
AS
  SELECT
    [No_] = c.[No.],
    Status = isnull((SELECT
                       Optionswert
                     FROM [BVK$MyBVK Optionswerte]
                     WHERE Feldname = N'Status' AND Integerwert = c.Status), null),
    MyBVKLogin = cast(c.[No.] AS INT),
    MyBVKPasswort = CASE c.[MyBVKPasswort]
    WHEN '' THEN N'afd98a6fds976sgjz9f68fd6gjfd6g987fdg6'
--when 'bvk' then c.[MyBVKLogin] + 'bvk' + c.[MyBVKLogin]
    ELSE c.[MyBVKPasswort]
    END,
    [VSName1] = dbo.bvkplatform_isnull(c.[VSName1]),
    [VSName2] = dbo.bvkplatform_isnull(c.[VSName2]),
    [VSName3] = dbo.bvkplatform_isnull(c.[VSName3]),
    [Address 2] = dbo.bvkplatform_isnull(c.[Address 2]),
    [Post Code] = dbo.bvkplatform_isnull(c.[Post Code]),
    [City] = dbo.bvkplatform_isnull(c.City),
    [Country Code] = CASE c.[Country/Region Code]
    WHEN '' THEN N'D'
    ELSE c.[Country/Region Code]
    END,
    [Phone No_] = dbo.bvkplatform_isnull(c.[Phone No.]),
    [Fax No_] = dbo.bvkplatform_isnull(c.[Fax No.]),
    [Mobile Phone No_] = dbo.bvkplatform_isnull(c.[Mobile Phone No.]),
    [Phone No_ privat] = dbo.bvkplatform_isnull(c.[Phone No. private]),
    [Fax No_ privat] = dbo.bvkplatform_isnull(c.[Fax No. private]),
    [Post-office box] = dbo.bvkplatform_isnull(c.[Post-office box]),
    [Post Code Post-office box] = dbo.bvkplatform_isnull(c.[Post Code Post-office box]),
    [City Post-office box] = dbo.bvkplatform_isnull(c.[City Post-office box]),
    [E-Mail] = dbo.bvkplatform_isnull(c.[E-Mail]),
    [E-Mail Newsletter] = dbo.bvkplatform_isnull(c.[EMail2]),
    [Home Page] = dbo.bvkplatform_isnull(c.[Home Page]),
    [Salutation Code] = CASE c.[Salutation Code]
    WHEN 'OFIRMA' THEN null
    ELSE c.[Salutation Code]
    END,
    [Correspondence Salutation Code] = dbo.bvkplatform_isnull(c.[Correspondence Salutation Code]),
    [Bezirksverband] = dbo.bvkplatform_isnull(bv.Beschreibung),
    [Regionalverband] = dbo.bvkplatform_isnull(rv.Beschreibung),
    [Function Codes] = dbo.bvkplatform_isnull(dbo.bvkplatform_getfunctions2(c.[No.])),
    [Ehrenamt] = replace(
        replace(
            replace(dbo.bvkplatform_isnull(dbo.bvkplatform_getfunctions(c.[No.])),
                    'Bezirksverband ', 'BV - '),
            'Regionalverband ', 'RV - '),
        ', ', ','),
    [Companies] = dbo.bvkplatform_isnull(dbo.bvkplatform_getcompanies(c.[No.])),
    [Ausbildung] = dbo.bvkplatform_isnull(dbo.bvkplatform_geteducation(c.[No.])),
    [Birth Date] = dbo.bvkplatform_date(c.[Birth Date]),
    [Begin Membership] = dbo.bvkplatform_date(c.[Begin Membership]),
    [End Membership] = dbo.bvkplatform_date(c.[End Membership]),
    [FirmengruendungAm] = dbo.bvkplatform_date(c.[FirmengruendungAm]),
    [PensionaerSeit] = dbo.bvkplatform_date(c.[PensionaerSeit]),
    [Rechtsform] = isnull((SELECT
                       Optionswert = CASE Optionswert
                       WHEN '' THEN null ELSE Optionswert END
                           FROM [BVK$MyBVK Optionswerte]
                           WHERE Feldname = N'Rechtsform' AND Integerwert = c.Rechtsform), null),
    [Handelsregistereintrag] = isnull((SELECT
                       Optionswert = CASE Optionswert
                       WHEN '' THEN null ELSE Optionswert END
                                       FROM [BVK$MyBVK Optionswerte]
                                       WHERE Feldname = N'Handelsregistereintrag' AND
                                             Integerwert = c.Handelsregistereintrag), null),
    [Vermittlerart] = isnull((SELECT
                       Optionswert = CASE Optionswert
                       WHEN '' THEN null ELSE Optionswert END
                              FROM [BVK$MyBVK Optionswerte]
                              WHERE Feldname = N'Vermittlerart' AND Integerwert = c.Vermittlerart), null),
    [Sparte] = isnull((SELECT
                       Optionswert = CASE Optionswert
                       WHEN '' THEN null ELSE Optionswert END
                       FROM [BVK$MyBVK Optionswerte]
                       WHERE Feldname = N'Sparte' AND Integerwert = c.Sparte), null),
    [MitgliedschaftBestaetigtAm] = dbo.bvkplatform_date(c.[MitgliedschaftBestaetigtAm]),
    [Beitragsgruppe] = dbo.bvkplatform_isnull(c.Beitragsgruppe),
    [Kündigungsdatum] = NULL,
--'Feld ist nicht bekannt!'
    [Kuendigungsgrund] = isnull((SELECT
                       Optionswert = CASE Optionswert
                       WHEN '' THEN null ELSE Optionswert END
                                 FROM [BVK$MyBVK Optionswerte]
                                 WHERE Feldname = N'Kuendigungsgrund' AND Integerwert = c.Kuendigungsgrund), null),
    [KuendigungBestaetigtAm] = dbo.bvkplatform_date(c.KuendigungBestaetigtAm),
    [Pensionär] = isnull((SELECT
                       Optionswert = CASE Optionswert
                       WHEN '' THEN null ELSE Optionswert END
                          FROM [BVK$MyBVK Optionswerte]
                          WHERE Feldname = N'PensionaerKZ' AND Integerwert = c.PensionaerKZ), null),
    [Lastschrift] = CASE c.[Payment Method Member]
    WHEN 'LAST' THEN N'Ja'
    ELSE N'Nein'
    END,
    [Search Name] = isnull(c.[Search Name], null)
  FROM
      dbo.[BVK$MyBVK Kontakte] p
      INNER JOIN dbo.[BVK$Contact] c
        ON p.[Contact No.] = c.[No.]
      LEFT OUTER JOIN dbo.[BVK$Bezirksverband] bv
        ON c.Bezirksverbandscode = bv.Code
      LEFT OUTER JOIN dbo.[BVK$Regionalverband] rv
        ON c.Regionalverbandscode = rv.Code
GO

--
--
--
--create procedure dbo.bvkplatform_updateall
--as
--    truncate table bvkplatform_contact
--	declare @num nvarchar(6)
--	declare @d datetime
----	declare @i int
----	set @i = 0
--	declare myc cursor for select [No_] from [BVK$Contact] where [Status] = 2 /*or [FktMyBVK] > 0*/
--	open myc;
--	fetch next from myc into @num
--	while @@fetch_status = 0
--	begin
--		update [BVK$Contact] set [Name 2] = [Name 2] where [No_] = @num
----		set @i = @i + 1
----		if @i = 100
----		begin
----			print '100 lines ' + convert(nvarchar(30), getdate(), 113)
----			set @i = 0
----		end
--		fetch next from myc into @num
--	end
--	close myc;
--	deallocate myc;
--go
