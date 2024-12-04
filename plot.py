import csv
import matplotlib.pyplot as plt

# Load the data manually from the CSV file
file_path = './output/results.csv'
data = []
with open(file_path, 'r') as file:
    reader = csv.DictReader(file)
    for row in reader:
        data.append({
            'name': row['name'],
            'comparisons': int(row['comparisons']),
            'time': float(row['time'])
        })

# Filter data to include only "Ints"
# filtered_data = [row for row in data if row['name'].startswith('Strings')]

# Group data by 'name'
grouped_data = {}
for row in data:
    if row['name'] not in grouped_data:
        grouped_data[row['name']] = {'comparisons': [], 'time': []}
    grouped_data[row['name']]['comparisons'].append(row['comparisons'])
    grouped_data[row['name']]['time'].append(row['time'])

# Plot the data
plt.figure(figsize=(10, 6))
for name, values in grouped_data.items():
    plt.plot(values['comparisons'], values['time'], marker='o', label=name)

# Add labels, legend, and title
plt.xlabel('#comparisons', fontsize=12)
plt.ylabel('Time (ms)', fontsize=12)
plt.title('Benchmark Times by Input Type', fontsize=14)
plt.legend(title='Input Types', fontsize=10)
plt.xscale('log')
plt.yscale('log')
plt.grid(True)
plt.tight_layout()
plt.show()