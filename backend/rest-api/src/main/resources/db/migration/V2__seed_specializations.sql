-- Seed catalog of legal specializations (MX context)
INSERT INTO specializations (code, name) VALUES
    ('civil',         'Derecho Civil'),
    ('penal',         'Derecho Penal'),
    ('laboral',       'Derecho Laboral'),
    ('familiar',      'Derecho Familiar'),
    ('mercantil',     'Derecho Mercantil'),
    ('fiscal',        'Derecho Fiscal'),
    ('administrativo','Derecho Administrativo'),
    ('amparo',        'Amparo'),
    ('migratorio',    'Derecho Migratorio'),
    ('inmobiliario',  'Derecho Inmobiliario'),
    ('corporativo',   'Derecho Corporativo'),
    ('propiedad_intelectual', 'Propiedad Intelectual')
ON CONFLICT (code) DO NOTHING;
