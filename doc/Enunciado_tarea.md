# UNIVERSIDAD ICESI

## INGENIERÍA DE SISTEMAS

### INGENIERÍA DE SOFTWARE IV

---

## TAREA ANÁLISIS – GRAFO DE ARCOS SITM-MIO

En el contexto del enunciado del problema de calcular la velocidad promedio de los arcos en las rutas del SITM-MIO, se tienen las siguientes tablas con datos correspondientes a la delimitación del problema:

- **RUTAS** (lines.csv): 105 rutas
- **PARADAS** (stops.csv): 2.119 paradas
- **Paradas Por Ruta** (linestops.csv): 7.368 paradas por ruta

---

## Ejemplo Gráfico

A continuación se muestra gráficamente un ejemplo:

Y las tres primeras filas de cada tabla:

### lines.csv

| LINEID | PLANVERSIONID | SHORTNAME | DESCRIPTION                                            | ACTIVATIONDATE          |
| ------ | ------------- | --------- | ------------------------------------------------------ | ----------------------- |
| 131    | 241           | T31       | Terminal Paso del Comercio - Universidades             | 2018-05-15 00:00:00.000 |
| 140    | 241           | T40       | Terminal Andrés Sanín - Centro                       | 2018-05-15 00:00:00.000 |
| 2102   | 241           | P10B      | Estación Universidades-El Ingenio-Estación-Estación | 2018-05-15 00:00:00.000 |

### Stops.csv

| STOPID | PLANVERSIONID | SHORTNAME | LONGNAME                | GPS_X      | GPS_Y    | DECIMALLONG  | DECIMALLATIT |
| ------ | ------------- | --------- | ----------------------- | ---------- | -------- | ------------ | ------------ |
| 511409 | 241           | K109C421  | Kr 109 con Cl 42        | -763106233 | 32130942 | -76.51839806 | 3.358595     |
| 511412 | 241           | K109C423  | Kr 109 con Cl 42        | -763105815 | 32131175 | -76.51828194 | 3.35865972   |
| 511413 | 241           | C42K1121  | Cl 42 entre Kr109 y 112 | -763113538 | 32122837 | -76.52042722 | 3.35634361   |

### Linestops.csv

| LINESTOPID | STOPSEQUENCE | ORIENTATION | LINEID | STOPID  | PLANVERSIONID | LINEVARIANT | LINEVARIANTTYPE |
| ---------- | ------------ | ----------- | ------ | ------- | ------------- | ----------- | --------------- |
| 1452640    | 34           | 0           | 2515   | 1446824 | 131           |             |                 |
| 1452641    | 35           | 0           | 2515   | 1417324 | 131           |             |                 |
| 1452642    | 36           | 0           | 2515   | 1427624 | 131           |             |                 |

### Datagrams.csv

| eventType | registerdate | stopId | odometer | latitude | longitude  | taskId | lineId | tripId | unknown1 | datagramDate        | busId |
| --------- | ------------ | ------ | -------- | -------- | ---------- | ------ | ------ | ------ | -------- | ------------------- | ----- |
| 0         | 28-MAY-19    | 511332 | 77034    | 761183   | -764873683 | 757224 | 115    | 913    | 65       | 2019-05-27 20:14:43 | 1069  |
| 0         | 28-MAY-19    | 514002 | 33344    | 75900    | -764885017 | 786227 | 322    | 913    | 66       | 2019-05-27 20:14:43 | 1075  |
| 0         | 28-MAY-19    | 500103 | 64348    | 93300    | -76507756  | 786822 | 417    | 513    | 87       | 2019-05-27 20:14:43 | 1171  |

---

## Descripción del Problema

Cada ruta está compuesta por una secuencia de paradas, y tiene dos sentidos: el de ida y el de regreso. En general, la secuencia de paradas de ida es distinta de la secuencia de paradas de regreso (columna `ORIENTATION`). Por lo tanto, cada ruta determina, en principio, dos grafos, en la que los nodos son las paradas, y las aristas son los arcos entre cada par de paradas como lo definen las rutas.

Para resolver el problema del enunciado del proyecto final, un primer ejercicio es construir los grafos de las rutas, dado que el objetivo es calcular las velocidades promedio de los arcos que corresponden a dichas rutas, es decir, los pesos de las aristas de los grafos.

Esta tarea consiste precisamente, en los grupos definidos, construir el grafo de arcos para los que hay que calcular las velocidades promedio, a partir de los tres archivos CSV. El extracto del archivo de datagramas es solo para que lo analicen en este contexto.

---

## Entregables

### A. Código en Java

Código en Java que, a partir de los tres archivos CSV, construya el grafo de paradas y arcos, y muestre la lista ordenadas de arcos y en secuencia (ida y vuelta) por ruta, en consola.

### B. (Bono-opcional) Visualización Gráfica

Código que muestre gráficamente el grafo, usando por ejemplo Java2D y exportando a jpg.
