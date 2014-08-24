DROP VIEW v_profile;
CREATE VIEW v_profile
AS
  SELECT
    u.uid,
    u.name,
    u.mail,
    f.fid,
    f.title,
    v.value
  FROM
      users u
      INNER JOIN profile_values v
        ON u.uid = v.uid
      INNER JOIN profile_fields f
        ON f.fid = v.fid
  ORDER BY
    f.fid
;
