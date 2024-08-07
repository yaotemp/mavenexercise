-- 定义过滤子查询
WITH filtered_data AS (
    SELECT *
    FROM your_table_name
    WHERE <your_filter_conditions> AND tax_return_type = 'A'
),

-- 计算 'A' 类型的百分位数
percentiles_A AS (
    SELECT
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
)

SELECT
    CASE
        WHEN f.doc_length <= p.p10 THEN p.p10
        WHEN f.doc_length <= p.p20 THEN p.p20
        WHEN f.doc_length <= p.p30 THEN p.p30
        WHEN f.doc_length <= p.p40 THEN p.p40
        WHEN f.doc_length <= p.p50 THEN p.p50
        WHEN f.doc_length <= p.p60 THEN p.p60
        WHEN f.doc_length <= p.p70 THEN p.p70
        WHEN f.doc_length <= p.p80 THEN p.p80
        WHEN f.doc_length <= p.p90 THEN p.p90
        ELSE NULL
    END AS length_range,
    COUNT(*) AS count,
    DECIMAL((COUNT(*) * 100.0) / (SELECT COUNT(*) FROM filtered_data), 5, 2) AS percentage
FROM filtered_data f, percentiles_A p
GROUP BY
    CASE
        WHEN f.doc_length <= p.p10 THEN p.p10
        WHEN f.doc_length <= p.p20 THEN p.p20
        WHEN f.doc_length <= p.p30 THEN p.p30
        WHEN f.doc_length <= p.p40 THEN p.p40
        WHEN f.doc_length <= p.p50 THEN p.p50
        WHEN f.doc_length <= p.p60 THEN p.p60
        WHEN f.doc_length <= p.p70 THEN p.p70
        WHEN f.doc_length <= p.p80 THEN p.p80
        WHEN f.doc_length <= p.p90 THEN p.p90
        ELSE NULL
    END
ORDER BY length_range;
