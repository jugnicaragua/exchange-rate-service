-- =================================================================================================
-- Importar monedas
-- =================================================================================================
INSERT INTO currency (
    iso_numeric_code
    , iso_string_code
    , symbol
    , short_description
    , description
    , is_domestic
    , is_active)
VALUES ('558', 'NIO', 'C$', 'CORDOBA', 'CORDOBA NICARAGÜENSE', true, true)
, ('840', 'USD', '$', 'DOLAR', 'DOLAR ESTADOUNIDENSE', false, true);
