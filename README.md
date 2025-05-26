# Documentación y rúbrica del proyecto

## El proyecto tiene la siguiente estructura:

### |-JavaImplementation:
Donde vamos a trabajar los diferentes métodos o algoritmos.
Esta carpeta se compone de las siguientes clases:
**-DistanceMatrix:**
Para generar la matriz de distancias, es decir, definir las distancias entre cada par de nodos (implementados como Points)
**-Methods:**
Aquí se implementarán los algoritmos, NNA es el algoritmo por defecto con el cuál haremos las comparaciones.
**-PathPanel:**
En esta clase se implementan funciones para poder graficar los resultados (Camino generado) usando Swing
**-Point:**
Clase para definir cada uno de los puntos que establecerán las rutas.
**-Reader:**
Clase para leer y generar la lista de puntos desde el archivo de coordenadas Coord.txt

### |-VRP_example:
donde se encuentra la docuemntación dada por el profesor para poder hacer las pruebas (leer el pdf)
De aquí nos guiaremos para hacer todas las pruebas e implementaciones. 
Ya queda una base hecha en la implementación de java.

## Explicación del algoritmo implementado (NNA):
El algoritmo del vecino más cercano (Nearest Neighbor) es un enfoque heurístico utilizado principalmente en problemas de optimización, como el Problema del Viajante (TSP). Su objetivo es construir una solución aproximada de manera rápida y sencilla.

### Funcionamiento:
**Inicio:** Selecciona un nodo inicial (ciudad, punto, etc.) arbitrariamente o según un criterio específico.
Selección del vecino más cercano: Desde el nodo actual, elige el nodo más cercano que aún no haya sido visitado. La "cercanía" se mide generalmente en términos de distancia (euclidiana).
Marcado como visitado: Marca el nodo seleccionado como visitado.
**Repetición:** Repite el proceso hasta que todos los nodos hayan sido visitados.
Cierre del ciclo (opcional): Si el problema requiere un ciclo (como en el TSP), regresa al nodo inicial para completar el recorrido.
**Ventajas:**
Es simple de implementar.
Es rápido, con una complejidad de O(n²) en su forma básica.
**Desventajas:**
No garantiza la solución óptima, ya que es un algoritmo greedy (codicioso) y toma decisiones locales sin considerar el impacto global.
Puede quedar atrapado en soluciones subóptimas.
**Ejemplo:**
Si tienes 4 ciudades (A, B, C, D) y las distancias entre ellas, el algoritmo podría funcionar así:

Empieza en A.
Encuentra la ciudad más cercana a A (por ejemplo, B).
Desde B, encuentra la ciudad más cercana no visitada (por ejemplo, C).
Desde C, visita la última ciudad no visitada (D).
Regresa a A si es necesario.
Este enfoque es útil como punto de partida o para problemas donde la precisión no es crítica.

## Tareas por hacer
### Implementación de nuestro algoritmo
Como pudimos ver en las desventajas del NNA, este no hace una solución  óptima al VRP, por lo que el objetivo principal del algoritmo de nosotros es hacerlo lo más óptimo posible. **Nota: TODO EL CÓDIGO DEBE ESTAR EN INGLÉS**
**Tarea 1:** Cada uno deberá pensar en al menos 1 método que nos sirva para intentar volver más optima la solución (se puede partir del NNA), considerando todo lo visto en clase.
**Tarea 2:** Hacer el pseudo código de la implementación y explicar porqué el método mejoraría la optimización de la solución.
**Tarea 3:** Enviar por el grupo el (los) método(s) para discutir al respecto.
 
### Documentación del proyecto en LaTex
Debemos hacer toda la presentación del proyecto de investigación en inglés y usando latex.
## Tenemos 2 semanas para terminar el proyecto.