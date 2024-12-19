import csv
import matplotlib.pyplot as plt
import numpy as np

file_path = "./output/task12results.csv"

data = {
    "MergeSortParallel": {},
    "MergeSort": {},
    "MergeSortWithInsertion": {}
}

with open(file_path, 'r') as file:
    reader = csv.reader(file)
    next(reader)
    for row in reader:
        algorithm, size, time, thread = row[0], int(row[1]), float(row[2]), int(
            row[3])
        if size not in data[algorithm]:
            data[algorithm][size] = []
        if algorithm == "MergeSortParallel":
            data[algorithm][size].append((thread, time))
        else:
            data[algorithm][size].append(time)

fig, ax = plt.subplots(figsize=(12, 8))

for size, values in data["MergeSortParallel"].items():
    best_time = min(values, key=lambda x: x[1])
    thread, time = best_time
    ax.plot(size, time, label=f"MergeSortParallel (n={size}, cutoff={thread})",
            color='blue', marker='o', linestyle='-', markersize=8)

for algorithm in ["MergeSort", "MergeSortWithInsertion"]:
    x = sorted(data[algorithm].keys())
    y = [min(data[algorithm][n]) for n in x]
    ax.plot(x, y, label=algorithm, color='red',
            marker='x' if algorithm == "MergeSort" else 's', linestyle='--',
            markersize=8)

ax.set_title("MergeSort Performance Comparison", fontsize=16)
ax.set_xlabel("Input Size (N)", fontsize=14)
ax.set_ylabel("Time (ms)", fontsize=14)

ax.set_xscale("log", base=2)

x_ticks = sorted(data["MergeSortParallel"].keys())
ax.set_xticks(x_ticks)
ax.set_xticklabels([f"$2^{{{int(np.log2(n))}}}$" for n in x_ticks])

ax.set_yscale("log")
ax.legend(loc="upper left", fontsize=10)
ax.grid(True, which="both", linestyle="--", linewidth=0.5)

plt.tight_layout()
plt.show()
