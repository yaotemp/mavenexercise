-- Calculate percentiles for each tax_return_type
WITH percentiles AS (
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
    FROM your_table_name
    GROUP BY tax_return_type
)
SELECT
    t.tax_return_type,
    CASE
        WHEN t.doc_length <= p.p10 THEN '0-' || CAST(p.p10 AS VARCHAR(10))
        WHEN t.doc_length <= p.p20 THEN CAST(p.p10 AS VARCHAR(10)) || '-' || CAST(p.p20 AS VARCHAR(10))
        WHEN t.doc_length <= p.p30 THEN CAST(p.p20 AS VARCHAR(10)) || '-' || CAST(p.p30 AS VARCHAR(10))
        WHEN t.doc_length <= p.p40 THEN CAST(p.p30 AS VARCHAR(10)) || '-' || CAST(p.p40 AS VARCHAR(10))
        WHEN t.doc_length <= p.p50 THEN CAST(p.p40 AS VARCHAR(10)) || '-' || CAST(p.p50 AS VARCHAR(10))
        WHEN t.doc_length <= p.p60 THEN CAST(p.p50 AS VARCHAR(10)) || '-' || CAST(p.p60 AS VARCHAR(10))
        WHEN t.doc_length <= p.p70 THEN CAST(p.p60 AS VARCHAR(10)) || '-' || CAST(p.p70 AS VARCHAR(10))
        WHEN t.doc_length <= p.p80 THEN CAST(p.p70 AS VARCHAR(10)) || '-' || CAST(p.p80 AS VARCHAR(10))
        WHEN t.doc_length <= p.p90 THEN CAST(p.p80 AS VARCHAR(10)) || '-' || CAST(p.p90 AS VARCHAR(10))
        ELSE CAST(p.p90 AS VARCHAR(10)) || '+'
    END AS length_range,
    COUNT(*) AS count,
    DECIMAL((COUNT(*) * 100.0) / (SELECT COUNT(*) FROM your_table_name WHERE tax_return_type = t.tax_return_type), 5, 2) AS percentage
FROM your_table_name t
JOIN percentiles p ON t.tax_return_type = p.tax_return_type
GROUP BY
    t.tax_return_type,
    CASE
        WHEN t.doc_length <= p.p10 THEN '0-' || CAST(p.p10 AS VARCHAR(10))
        WHEN t.doc_length <= p.p20 THEN CAST(p.p10 AS VARCHAR(10)) || '-' || CAST(p.p20 AS VARCHAR(10))
        WHEN t.doc_length <= p.p30 THEN CAST(p.p20 AS VARCHAR(10)) || '-' || CAST(p.p30 AS VARCHAR(10))
        WHEN t.doc_length <= p.p40 THEN CAST(p.p30 AS VARCHAR(10)) || '-' || CAST(p.p40 AS VARCHAR(10))
        WHEN t.doc_length <= p.p50 THEN CAST(p.p40 AS VARCHAR(10)) || '-' || CAST(p.p50 AS VARCHAR(10))
        WHEN t.doc_length <= p.p60 THEN CAST(p.p50 AS VARCHAR(10)) || '-' || CAST(p.p60 AS VARCHAR(10))
        WHEN t.doc_length <= p.p70 THEN CAST(p.p60 AS VARCHAR(10)) || '-' || CAST(p.p70 AS VARCHAR(10))
        WHEN t.doc_length <= p.p80 THEN CAST(p.p70 AS VARCHAR(10)) || '-' || CAST(p.p80 AS VARCHAR(10))
        WHEN t.doc_length <= p.p90 THEN CAST(p.p80 AS VARCHAR(10)) || '-' || CAST(p.p90 AS VARCHAR(10))
        ELSE CAST(p.p90 AS VARCHAR(10)) || '+'
    END
ORDER BY t.tax_return_type, length_range;
