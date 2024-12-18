import argparse
import csv
import matplotlib.pyplot as plt
import numpy as np

parser = argparse.ArgumentParser(description="plot task 2")
parser.add_argument('file_path', type=str, help="input path csv fiile")
#parser.add_argument('data_type_filter', type=str,
 #                   help="Which data types should be in plot?")
parser.add_argument('output_path', type=str,
                    help="output path")
args = parser.parse_args()

data = []
with open(args.file_path, 'r') as file:
    reader = csv.DictReader(file)
    for row in reader:
        data.append({
            'name': row['name'],
            'comparisons': int(row['comparisons']),
            'time': float(row['time'])
        })

# filtered_data = [row for row in data if
                 #row['name'].startswith(args.data_type_filter)]

grouped_data = {}
for row in data:
    if row['name'] not in grouped_data:
        grouped_data[row['name']] = {'comparisons': [], 'time': []}
    grouped_data[row['name']]['comparisons'].append(row['comparisons'])
    grouped_data[row['name']]['time'].append(row['time'])

plt.figure(figsize=(10, 6))
for name, values in grouped_data.items():
    plt.plot(values['comparisons'], values['time'], marker='o', label=name)

plt.xlabel('# Comparisons', fontsize=12)
plt.ylabel('Time (ms)', fontsize=12)
plt.title('Relationship between comparisons and running times for different input distributions', fontsize=14)
plt.legend(title='Input Types', fontsize=10)
plt.xscale('log')
plt.yscale('log')
plt.grid(True)
plt.tight_layout()

plt.savefig(args.output_path)
