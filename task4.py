import argparse
import csv
import matplotlib.pyplot as plt

parser = argparse.ArgumentParser(description="plot")
parser.add_argument('file_path', type=str, help="Path to the input CSV file.")
parser.add_argument('output_path', type=str,
                    help="Path to the output SVG file.")
args = parser.parse_args()


data = []
with open(args.file_path, 'r') as file:
    reader = csv.DictReader(file)
    for row in reader:
        data.append({
            'name': row['name'],
            'time': float(row['time']),
            'c': int(row['c'])
        })

grouped_data = {}
for row in data:
    if row['name'] not in grouped_data:
        grouped_data[row['name']] = {'c': [], 'time': []}
    grouped_data[row['name']]['c'].append(row['c'])
    grouped_data[row['name']]['time'].append(row['time'])

plt.figure(figsize=(10, 6))
for name, values in grouped_data.items():
    plt.plot(values['c'], values['time'], marker='o', label=name)

plt.xlabel('Parameter c', fontsize=12)
plt.ylabel('Avgerage time to sort N = 100000 (ms)', fontsize=12)
plt.title('Insertion Threshold effect on running time for a fixed problem size',
          fontsize=14)
plt.legend(title='Input Types', fontsize=10)
plt.grid(True)
plt.tight_layout()
plt.savefig(args.output_path)
