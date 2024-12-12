## TODO

double check experiment stuff

+ comparisons correct?
+ new input for each iteration?
+ decide good params

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

