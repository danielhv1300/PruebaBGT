SELECT DISTINCT c.nombre, c.apellidos
FROM Cliente c
JOIN Inscripcion i ON c.id = i.idCliente
JOIN Producto p ON i.idProducto = p.id
WHERE NOT EXISTS (
    SELECT 1
    FROM Disponibilidad d
    WHERE d.idProducto = p.id
    AND d.idSucursal NOT IN (
        SELECT v.idSucursal
        FROM Visitan v
        WHERE v.idCliente = c.id
    )
);