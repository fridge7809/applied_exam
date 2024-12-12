import argparse
import csv
import matplotlib.pyplot as plt
import numpy as np

# Argument parser for input arguments
parser = argparse.ArgumentParser(description="Plot data from a CSV file.")
parser.add_argument('file_path', type=str, help="Path to the input CSV file.")
parser.add_argument('data_type_filter', type=str,
                    help="Which data types should be in plot?")
parser.add_argument('output_path', type=str,
                    help="Path to save the output plot.")
args = parser.parse_args()

# Read data from CSV
data = []
with open(args.file_path, 'r') as file:
    reader = csv.DictReader(file)
    for row in reader:
        data.append({
            'name': row['name'],
            'comparisons': int(row['comparisons']),
            'time': float(row['time'])
        })

# Filter data based on the data type filter
filtered_data = [row for row in data if
                 row['name'].startswith(args.data_type_filter)]

# Group data by name
grouped_data = {}
for row in filtered_data:
    if row['name'] not in grouped_data:
        grouped_data[row['name']] = {'comparisons': [], 'time': []}
    grouped_data[row['name']]['comparisons'].append(row['comparisons'])
    grouped_data[row['name']]['time'].append(row['time'])

# Plot the data
plt.figure(figsize=(10, 6))
for name, values in grouped_data.items():
    plt.plot(values['comparisons'], values['time'], marker='o', label=name)

# Calculate and plot nlogn for comparisons
all_comparisons = sorted(
    {c for values in grouped_data.values() for c in values['comparisons']})
nlogn_values = [n * np.log(n) if n > 0 else 0 for n in all_comparisons]

# Normalize nlogn to fit the time scale for comparison
time_scale = max(max(values['time']) for values in grouped_data.values())
nlogn_scale = max(nlogn_values)
nlogn_values_normalized = [v * time_scale / nlogn_scale for v in nlogn_values]

plt.plot(all_comparisons, nlogn_values_normalized, '--',
         label='n log n (scaled)', color='gray')

# Configure plot
plt.xlabel('#comparisons', fontsize=12)
plt.ylabel('Time (ms)', fontsize=12)
plt.title('Benchmark Times by Input Type', fontsize=14)
plt.legend(title='Input Types', fontsize=10)
plt.xscale('log')
plt.yscale('log')
plt.grid(True)
plt.tight_layout()

# Save the plot to the specified path
plt.savefig(args.output_path)
