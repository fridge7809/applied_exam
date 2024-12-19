import csv
import matplotlib.pyplot as plt
import numpy as np

filename = "./output/task10results.csv"
data = {}
input_sizes = set()

with open(filename, 'r') as file:
    reader = csv.reader(file)
    header = next(reader)  # Skip the header
    for row in reader:
        algorithm, n, time = row[0], int(row[1]), float(row[2])
        if n not in data:
            data[n] = {}
        data[n][algorithm] = time
        input_sizes.add(n)

input_sizes = sorted(input_sizes)
algorithms = sorted(set(alg for size in data.values() for alg in size.keys()))

bar_width = 0.1
x_indexes = np.arange(len(input_sizes))
positions = []

plt.figure(figsize=(15, 8))

for i, algorithm in enumerate(algorithms):
    times = [data[n].get(algorithm, 0) for n in input_sizes]
    positions.append(x_indexes + i * bar_width)
    plt.bar(x_indexes + i * bar_width, times, bar_width, label=algorithm)

plt.xticks(x_indexes + (len(algorithms) - 1) * bar_width / 2,
           [f"N={n}" for n in input_sizes])
plt.yscale('log')
plt.xlabel("Input Sizes (N)")
plt.ylabel("Execution Time (ms)")
plt.title("Algorithm Performance by Input Size")
plt.legend()
plt.grid(axis='y', linestyle='--', linewidth=0.5)
plt.tight_layout()

plt.savefig("output/task10results.svg")
