WITH percentiles AS (
    SELECT
        tax_return_type,
        PERCENTILE_CONT(0.1) WITHIN GROUP (ORDER BY doc_length) OVER (PARTITION BY tax_return_type) AS p10,
        PERCENTILE_CONT(0.2) WITHIN GROUP (ORDER BY doc_length) OVER (PARTITION BY tax_return_type) AS p20,
        PERCENTILE_CONT(0.3) WITHIN GROUP (ORDER BY doc_length) OVER (PARTITION BY tax_return_type) AS p30,
        PERCENTILE_CONT(0.4) WITHIN GROUP (ORDER BY doc_length) OVER (PARTITION BY tax_return_type) AS p40,
        PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY doc_length) OVER (PARTITION BY tax_return_type) AS p50,
        PERCENTILE_CONT(0.6) WITHIN GROUP (ORDER BY doc_length) OVER (PARTITION BY tax_return_type) AS p60,
        PERCENTILE_CONT(0.7) WITHIN GROUP (ORDER BY doc_length) OVER (PARTITION BY tax_return_type) AS p70,
        PERCENTILE_CONT(0.8) WITHIN GROUP (ORDER BY doc_length) OVER (PARTITION BY tax_return_type) AS p80,
        PERCENTILE_CONT(0.9) WITHIN GROUP (ORDER BY doc_length) OVER (PARTITION BY tax_return_type) AS p90
    FROM your_table_name
)
SELECT
    tax_return_type,
    CASE
        WHEN doc_length <= p10 THEN '0-' || CAST(p10 AS VARCHAR(10))
        WHEN doc_length <= p20 THEN CAST(p10 AS VARCHAR(10)) || '-' || CAST(p20 AS VARCHAR(10))
        WHEN doc_length <= p30 THEN CAST(p20 AS VARCHAR(10)) || '-' || CAST(p30 AS VARCHAR(10))
        WHEN doc_length <= p40 THEN CAST(p30 AS VARCHAR(10)) || '-' || CAST(p40 AS VARCHAR(10))
        WHEN doc_length <= p50 THEN CAST(p40 AS VARCHAR(10)) || '-' || CAST(p50 AS VARCHAR(10))
        WHEN doc_length <= p60 THEN CAST(p50 AS VARCHAR(10)) || '-' || CAST(p60 AS VARCHAR(10))
        WHEN doc_length <= p70 THEN CAST(p60 AS VARCHAR(10)) || '-' || CAST(p70 AS VARCHAR(10))
        WHEN doc_length <= p80 THEN CAST(p70 AS VARCHAR(10)) || '-' || CAST(p80 AS VARCHAR(10))
        WHEN doc_length <= p90 THEN CAST(p80 AS VARCHAR(10)) || '-' || CAST(p90 AS VARCHAR(10))
        ELSE CAST(p90 AS VARCHAR(10)) || '+'
    END AS length_range,
    COUNT(*) AS count,
    DECIMAL((COUNT(*) * 100.0) / (SELECT COUNT(*) FROM your_table_name WHERE tax_return_type = t.tax_return_type), 5, 2) AS percentage
FROM your_table_name t
JOIN percentiles p ON t.tax_return_type = p.tax_return_type
GROUP BY
    tax_return_type,
    CASE
        WHEN doc_length <= p10 THEN '0-' || CAST(p10 AS VARCHAR(10))
        WHEN doc_length <= p20 THEN CAST(p10 AS VARCHAR(10)) || '-' || CAST(p20 AS VARCHAR(10))
        WHEN doc_length <= p30 THEN CAST(p20 AS VARCHAR(10)) || '-' || CAST(p30 AS VARCHAR(10))
        WHEN doc_length <= p40 THEN CAST(p30 AS VARCHAR(10)) || '-' || CAST(p40 AS VARCHAR(10))
        WHEN doc_length <= p50 THEN CAST(p40 AS VARCHAR(10)) || '-' || CAST(p50 AS VARCHAR(10))
        WHEN doc_length <= p60 THEN CAST(p50 AS VARCHAR(10)) || '-' || CAST(p60 AS VARCHAR(10))
        WHEN doc_length <= p70 THEN CAST(p60 AS VARCHAR(10)) || '-' || CAST(p70 AS VARCHAR(10))
        WHEN doc_length <= p80 THEN CAST(p70 AS VARCHAR(10)) || '-' || CAST(p80 AS VARCHAR(10))
        WHEN doc_length <= p90 THEN CAST(p80 AS VARCHAR(10)) || '-' || CAST(p90 AS VARCHAR(10))
        ELSE CAST(p90 AS VARCHAR(10)) || '+'
    END
ORDER BY tax_return_type, length_range;
