-- 对表进行过滤，然后计算每个 tax_return_type 的百分位数
WITH filtered_data AS (
    SELECT *
    FROM your_table_name
    WHERE <your_filter_conditions> -- 在这里加入你的过滤条件
),
percentiles AS (
    SELECT
        tax_return_type,
        PERCENTILE_CONT(0.1) WITHIN GROUP (ORDER BY doc_length) AS p10,
        PERCENTILE_CONT(0.2) WITHIN GROUP (ORDER BY doc_length) AS p20,
        PERCENTILE_CONT(0.3) WITHIN GROUP (ORDER BY doc_length) AS p30,
        PERCENTILE_CONT(0.4) WITHIN GROUP (ORDER BY doc_length) AS p40,
        PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY doc_length) AS p50,
        PERCENTILE_CONT(0.6) WITHIN GROUP (ORDER BY doc_length) AS p60,
        PERCENTILE_CONT(0.7) WITHIN GROUP (ORDER BY doc_length) AS p70,
        PERCENTILE_CONT(0.8) WITHIN GROUP (ORDER BY doc_length) AS p80,
        PERCENTILE_CONT(0.9) WITHIN GROUP (ORDER BY doc_length) AS p90
    FROM filtered_data
    GROUP BY tax_return_type
)
SELECT
    f.tax_return_type,
    CASE
        WHEN f.doc_length <= p.p10 THEN '0-' || CAST(p.p10 AS VARCHAR(10))
        WHEN f.doc_length <= p.p20 THEN CAST(p.p10 AS VARCHAR(10)) || '-' || CAST(p.p20 AS VARCHAR(10))
        WHEN f.doc_length <= p.p30 THEN CAST(p.p20 AS VARCHAR(10)) || '-' || CAST(p.p30 AS VARCHAR(10))
        WHEN f.doc_length <= p.p40 THEN CAST(p.p30 AS VARCHAR(10)) || '-' || CAST(p.p40 AS VARCHAR(10))
        WHEN f.doc_length <= p.p50 THEN CAST(p.p40 AS VARCHAR(10)) || '-' || CAST(p.p50 AS VARCHAR(10))
        WHEN f.doc_length <= p.p60 THEN CAST(p.p50 AS VARCHAR(10)) || '-' || CAST(p.p60 AS VARCHAR(10))
        WHEN f.doc_length <= p.p70 THEN CAST(p.p60 AS VARCHAR(10)) || '-' || CAST(p.p70 AS VARCHAR(10))
        WHEN f.doc_length <= p.p80 THEN CAST(p.p70 AS VARCHAR(10)) || '-' || CAST(p.p80 AS VARCHAR(10))
        WHEN f.doc_length <= p.p90 THEN CAST(p.p80 AS VARCHAR(10)) || '-' || CAST(p.p90 AS VARCHAR(10))
        ELSE CAST(p.p90 AS VARCHAR(10)) || '+'
    END AS length_range,
    COUNT(*) AS count,
    DECIMAL((COUNT(*) * 100.0) / (SELECT COUNT(*) FROM filtered_data WHERE tax_return_type = f.tax_return_type), 5, 2) AS percentage
FROM filtered_data f
JOIN percentiles p ON f.tax_return_type = p.tax_return_type
GROUP BY
    f.tax_return_type,
    CASE
        WHEN f.doc_length <= p.p10 THEN '0-' || CAST(p.p10 AS VARCHAR(10))
        WHEN f.doc_length <= p.p20 THEN CAST(p.p10 AS VARCHAR(10)) || '-' || CAST(p.p20 AS VARCHAR(10))
        WHEN f.doc_length <= p.p30 THEN CAST(p.p20 AS VARCHAR(10)) || '-' || CAST(p.p30 AS VARCHAR(10))
        WHEN f.doc_length <= p.p40 THEN CAST(p.p30 AS VARCHAR(10)) || '-' || CAST(p.p40 AS VARCHAR(10))
        WHEN f.doc_length <= p.p50 THEN CAST(p.p40 AS VARCHAR(10)) || '-' || CAST(p.p50 AS VARCHAR(10))
        WHEN f.doc_length <= p.p60 THEN CAST(p.p50 AS VARCHAR(10)) || '-' || CAST(p.p60 AS VARCHAR(10))
        WHEN f.doc_length <= p.p70 THEN CAST(p.p60 AS VARCHAR(10)) || '-' || CAST(p.p70 AS VARCHAR(10))
        WHEN f.doc_length <= p.p80 THEN CAST(p.p70 AS VARCHAR(10)) || '-' || CAST(p.p80 AS VARCHAR(10))
        WHEN f.doc_length <= p.p90 THEN CAST(p.p80 AS VARCHAR(10)) || '-' || CAST(p.p90 AS VARCHAR(10))
        ELSE CAST(p.p90 AS VARCHAR(10)) || '+'
    END
ORDER BY f.tax_return_type, length_range;
