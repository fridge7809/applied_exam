import csv
import matplotlib.pyplot as plt
import numpy as np


def read_csv(filename):
    data = []
    with open(filename, 'r') as file:
        reader = csv.reader(file)
        headers = next(reader)
        for row in reader:
            entry = {
                "mergerule": row[0],
                "isadaptive": row[1] == "true",
                "inputdistribution": row[2],
                "c": int(row[3]),
                "comparisons": int(row[4]),
                "time": float(row[5]),
            }
            data.append(entry)
    return data


def plot_data(data):
    conditions = [
        {"isadaptive": True, "inputdistribution": "UNIFORM"},
        {"isadaptive": True, "inputdistribution": "PRESORTED"},
    ]
    titles = [
        "Adaptive, Uniform Distribution",
        "Adaptive, Presorted Distribution",
    ]

    fig, axs = plt.subplots(1, 2, figsize=(15, 5), sharex=True, sharey=True)
    axs = axs.flatten()

    for i, condition in enumerate(conditions):
        subset = [d for d in data if
                  d["isadaptive"] == condition["isadaptive"] and
                  d["inputdistribution"] == condition["inputdistribution"]]

        if not subset:
            continue

        axs[i].set_title(titles[i])
        axs[i].set_xlabel("c value (log scale)")
        axs[i].set_ylabel("Time (s)")

        for rule in set(d["mergerule"] for d in subset):
            rule_subset = [d for d in subset if d["mergerule"] == rule]
            x = [d["c"] for d in rule_subset]
            y = [d["time"] for d in rule_subset]
            axs[i].plot(x, y, marker='o', label=rule)
        axs[i].legend()
        axs[i].set_xscale('log', base=2)
        axs[i].set_yscale('log', base=2)

        xticks = [2 ** i for i in
                  range(int(np.log2(min(d["c"] for d in subset))),
                        int(np.log2(max(d["c"] for d in subset))) + 1)]
        axs[i].set_xticks(xticks)
        axs[i].set_xticklabels([f'$2^{{{int(np.log2(t))}}}$' for t in xticks])

    plt.tight_layout()
    plt.savefig("output/task9results.svg")

if __name__ == "__main__":
    filename = "./output/task9results.csv"
    data = read_csv(filename)
    plot_data(data)
