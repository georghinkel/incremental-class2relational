#! /bin/bash
echo "Transformation Aspect, Value" > atol-metrics.csv
grep -E "// Setup|// Model Traversal|// Helper|// Expression Outsourcing|// Tracing|// Incrementality|// Transformation|// Model Navigation|// Change Propagation" ../../solutions/atol/transformation/src/main/java/atol/example/transformation/Class2Relational.xtend | sed -e 's/^[[:space:]]*\/\/\?//' | sed -e 's/\([0-9]\)/, \1/' | sed 's/Expression Outsourcing/Helper/g' >> atol-metrics.csv
grep -E "// Setup|// Model Traversal|// Helper|// Expression Outsourcing|// Tracing|// Incrementality|// Transformation|// Model Navigation|// Change Propagation" ../../solutions/atol/transformation/src/main/java/atol/example/transformation/Run.xtend | sed -e 's/^[[:space:]]*\/\/\?//' | sed -e 's/\([0-9]\)/, \1/' | sed 's/Expression Outsourcing/Helper/g' >> atol-metrics.csv
grep -E "\-\- Setup|\-\- Model Traversal|\-\- Helper|\-\- Expression Outsourcing|\-\- Tracing|\-\- Incrementality|\-\- Transformation" ../../solutions/atol/transformation/src/main/resources/Class2Relational.atl | sed -e 's/^[[:space:]]*--//' | sed -e 's/\([0-9]\)/, \1/' | sed 's/Expression Outsourcing/Helper/g' >> atol-metrics.csv
