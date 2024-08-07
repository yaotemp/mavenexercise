WITH percentiles AS (
    SELECT
        PERCENTILE_CONT(0.2) WITHIN GROUP (ORDER BY doc_length) AS p20,
        PERCENTILE_CONT(0.4) WITHIN GROUP (ORDER BY doc_length) AS p40,
        PERCENTILE_CONT(0.6) WITHIN GROUP (ORDER BY doc_length) AS p60,
        PERCENTILE_CONT(0.8) WITHIN GROUP (ORDER BY doc_length) AS p80
    FROM your_table_name
)
SELECT
    CASE
        WHEN doc_length <= p20 THEN '0-' || CAST(p20 AS VARCHAR(10))
        WHEN doc_length <= p40 THEN CAST(p20 AS VARCHAR(10)) || '-' || CAST(p40 AS VARCHAR(10))
        WHEN doc_length <= p60 THEN CAST(p40 AS VARCHAR(10)) || '-' || CAST(p60 AS VARCHAR(10))
        WHEN doc_length <= p80 THEN CAST(p60 AS VARCHAR(10)) || '-' || CAST(p80 AS VARCHAR(10))
        ELSE CAST(p80 AS VARCHAR(10)) || '+'
    END AS length_range,
    COUNT(*) AS count,
    DECIMAL((COUNT(*) * 100.0) / (SELECT COUNT(*) FROM your_table_name), 5, 2) AS percentage
FROM your_table_name, percentiles
GROUP BY
    CASE
        WHEN doc_length <= p20 THEN '0-' || CAST(p20 AS VARCHAR(10))
        WHEN doc_length <= p40 THEN CAST(p20 AS VARCHAR(10)) || '-' || CAST(p40 AS VARCHAR(10))
        WHEN doc_length <= p60 THEN CAST(p40 AS VARCHAR(10)) || '-' || CAST(p60 AS VARCHAR(10))
        WHEN doc_length <= p80 THEN CAST(p60 AS VARCHAR(10)) || '-' || CAST(p80 AS VARCHAR(10))
        ELSE CAST(p80 AS VARCHAR(10)) || '+'
    END
ORDER BY length_range;
