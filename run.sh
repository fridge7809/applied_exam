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
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkMergesort "16384,32768,65536,131072,262144" "50"


####### TASK 4 && TASK 7 ###########
# <algorithmname> <iterations> <parameter_C_upper_bound>
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkMergesortAugmented timsort "100" "60"
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkMergesortAugmented mergesort "100" "60"

####### TASK 9 ###########
# inputsize iterations exponentUpperBound
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkAdaptiveness "65536" "5" "13"

####### TASK 10 ###########
# inputsizeCommaSeperated iterations
java -cp build/libs/applied_exam-1.jar org.aaa.HorseRace "16384,32768,65536" "5"

####### TASK 12 ###########
# inputsizeCommaSeperated iterations
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkParallel "16384,32768,65536" "10" "1,12"


####### Plot all the generated data ###########
python3 task2.py "output/task2results.csv" "output/task2_plot.svg"
python3 task4.py "output/task4mergesortresults.csv" "output/task4mergesortplot.svg"
python3 task4.py "output/task4timsortresults.csv" "output/task4timsortresults.svg"
python3 task9.py "output/task9results.csv" "output/task9results.svg"
python3 task10.py "output/task10results.csv" "output/task10results.svg"
python3 task12.py "output/task12results.csv" "output/task12results.svg"

echo "results and plots in dir /output"
echo "DONE"