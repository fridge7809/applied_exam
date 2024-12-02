import matplotlib.pyplot as plt
import pandas as pd

# Load the CSV data from the specified path
csv_path = 'build/results/jmh/results.csv'
df = pd.read_csv(csv_path)

# Clean the "Score" column by converting the commas to dots and then casting to float
df['Score'] = df['Score'].str.replace(',', '.').astype(float)

# Plot the results
plt.figure(figsize=(10, 6))

# Plot each benchmark
for benchmark in df['Benchmark'].unique():
    subset = df[df['Benchmark'] == benchmark]
    plt.plot(subset['Param: n'], subset['Score'], label=benchmark)

# Labeling
plt.xlabel("Param: n")
plt.ylabel("Score (us/op)")
plt.title("Benchmark Performance")
plt.legend()
plt.grid(True)

# Use logarithmic scale for the x-axis (and optionally for y-axis) for better visualization
plt.xscale('log')
plt.yscale('log')

# Show the plot
plt.show()
