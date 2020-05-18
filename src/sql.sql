CREATE VIEW onlinetop AS
    SELECT usuario, tempojogado,
           FLOOR(tempoJogado/1440) AS dias,
           FLOOR((tempoJogado%1440)/60) AS horas,
           FLOOR(tempoJogado%60) AS minutos
    FROM edtempojogado ORDER BY tempoJogado DESC LIMIT 5




37 min
26 dia
14 ho



46min
29d
4h

