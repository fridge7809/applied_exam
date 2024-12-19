
### Build

```shell
./gradlew build
```

### Run tests

```shell
./gradlew clean test --info
```

### Run all experiments

```shell
chmod +x ./run.sh && ./run.sh
```

### Run task 2 experiment

```shell
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkMergesort "1000,10000,100000" "500"
```

### Plot task 2 experiment

```shell
python3 task2.py "output/task2results.csv" "INTS" "output/task2_ints_plot.svg" 
python3 task2.py "output/task2results.csv" "STRINGS" "output/task2_strings_plot.svg"
```

### Run task 4 experiment

```shell
# algorithm parameterRange warmups
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkMergesortAugmented mergesort "100" "0"
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkMergesortAugmented timsort "100" "0"
```

### Plot task 4 experiment

```shell
python3 task4.py "output/task4mergesortresults.csv" "output/task4mergesortplot.svg"
python3 task4.py "output/task4timsortresults.csv" "output/task4timsortresults.svg"
```

### Run task 9 experiment

```shell
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkAdaptiveness "65536" "5" "13"
```

### Plot task 9 experiment

```shell
python3 task9.py "output/task9results.csv" "output/task9results.svg"
```

### Run task 10 experiment

```shell
java -cp build/libs/applied_exam-1.jar org.aaa.HorseRace "16384,32768,65536" "5"
```

### Plot task 10 experiment

```shell
python3 task10.py "output/task10results.csv" "output/task10results.svg"
```

### Run task 12 experiment

```shell
java -cp build/libs/applied_exam-1.jar org.aaa.BenchmarkParallel "16384,32768,65536" "10" "1,12"
```

### Plot task 12 experiment

```shell
python3 task12.py "output/task12results.csv" "output/task12results.svg"
```