CREATE TABLE t_wp_appointment (
    bv VARCHAR(100)
    , sched_id BIGINT
    , post_id BIGINT
    , start DATETIME
    , end DATETIME
    , allday TINYINT(1)
    , post_title TEXT
    , guid VARCHAR(255)
    , post_content LONGTEXT
)
