#!/bin/bash

COOKIE_FILE="./output/dependency_check.cookie"

mkdir -p ./output

java --version
conda --version

if [[ -f "$COOKIE_FILE" ]]; then
  echo "Dependencies OK"
else
  echo "Please confirm the following dependencies are installed on your machine:"
  echo "OpenJDK >= 22.0.1; Anaconda >= 24.7.1"

  read -p "Are these dependencies installed? (yes/no): " user_input

  if [[ "$user_input" == "no" ]]; then
    exit 1
  fi

  echo "Dependencies OK on $(date)" > "$COOKIE_FILE"
fi

./gradlew clean
./gradlew build

if [[ $? -ne 0 ]]; then
  echo "Build failed. Exiting."
  exit 1
fi

echo "Build and test OK"

# problemSize iterations
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkMergesort "1000,10000,100000" "25"
# algorithm parameterRange warmups
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkMergesortAugmented mergesort "100" "5"
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkMergesortAugmented timsort "100" "5"

python3 task2.py "output/task2results.csv" "Ints" "output/task2_ints_plot.svg"
python3 task2.py "output/task2results.csv" "Strings" "output/task2_strings_plot.svg"
python3 task4.py "output/task4mergesortresults.csv" "output/task4mergesortplot.svg"
python3 task4.py "output/task4timsortresults.csv" "output/task4timsortresults.svg"

echo "results and plots in dir /output"
echo "DONE"