#!/bin/bash
COOKIE_FILE="./output/dependency_check.cookie"

mkdir -p ./output

java --version
conda --version

if [[ -f "$COOKIE_FILE" ]]; then
  echo "Dependencies OK"
else
  echo "OpenJDK >= 22.0.1; Anaconda >= 24.7.1"
  read -p "Are these dependencies installed? (yes/no): " user_input

  if [[ "$user_input" == "no" ]]; then
    exit 1
  fi

  echo "Dependencies OK on $(date)" > "$COOKIE_FILE"
fi

./gradlew clean
./gradlew build

echo "Build and test OK"

####### TASK 2 ###########
# <problemsize> <iterations>
# java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkMergesort "16384,32768,65536,131072,262144" "50"


####### TASK 4 && TASK 7 ###########
# <algorithmname> <iterations> <parameter_C_upper_bound>
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkMergesortAugmented timsort "100" "60"
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkMergesortAugmented mergesort "100" "60"

####### TASK 9 ###########
# inputsize iterations exponentUpperBound
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkAdaptiveness "65536" "5" "15"

## DEPRECATED - for plotting data types separately
#python3 task2.py "output/task2results.csv" "INTS" "output/task2_ints_plot.svg"
#python3 task2.py "output/task2results.csv" "STRINGS" "output/task2_strings_plot.svg"

python3 task4.py "output/task4mergesortresults.csv" "output/task4mergesortplot.svg"
python3 task4.py "output/task4timsortresults.csv" "output/task4timsortresults.svg"

echo "results and plots in dir /output"
echo "DONE"