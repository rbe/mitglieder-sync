SELECT
        s.sched_id
        , s.post_id
        , s.start
        , s.end
        , s.allday
        , p.post_content
        , p.post_title
        , p.guid
        , 'bvk_muenster' bv
  FROM
        bvk_muenster.wp_ec3_schedule s
        INNER JOIN bvk_muenster.wp_posts p ON s.post_id = p.ID
UNION ALL
...
