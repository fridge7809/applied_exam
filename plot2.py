import csv
import matplotlib.pyplot as plt

# Load the data manually from the CSV file
file_path = './output/results2.csv'
data = []
with open(file_path, 'r') as file:
    reader = csv.DictReader(file)
    for row in reader:
        data.append({
            'name': row['name'],
            'time': float(row['time']),
            'c': int(row['c'])  # Adjusting for 'c' instead of 'time'
        })

# Group data by 'name'
grouped_data = {}
for row in data:
    if row['name'] not in grouped_data:
        grouped_data[row['name']] = {'c': [], 'time': []}
    grouped_data[row['name']]['c'].append(row['c'])
    grouped_data[row['name']]['time'].append(row['time'])

# Plot the data
plt.figure(figsize=(10, 6))
for name, values in grouped_data.items():
    plt.plot(values['c'], values['time'], marker='o', label=name)

# Add labels, legend, and title
plt.xlabel('Parameter c', fontsize=12)
plt.ylabel('Avgerage time to sort N = 100000 (ms)', fontsize=12)
plt.title('Insertion Threshold effect on running time for a fixed problem size',
          fontsize=14)
plt.legend(title='Input Types', fontsize=10)
plt.grid(True)
plt.tight_layout()
plt.show()
