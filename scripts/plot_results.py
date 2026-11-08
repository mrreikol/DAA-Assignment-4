"""Render measured averages; requires Python 3 and matplotlib."""
import csv
from pathlib import Path
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parents[1]
rows = list(csv.DictReader((ROOT / 'results/summary.csv').open()))
plt.rcParams.update({'font.size': 11, 'axes.spines.top': False,
                     'axes.spines.right': False, 'figure.dpi': 150})

def series(ax, algorithm, scenario):
    values = [r for r in rows if r['algorithm'] == algorithm and r['scenario'] == scenario]
    ax.plot([int(r['n']) for r in values], [float(r['average_ms']) for r in values],
            marker='o', label=algorithm, linewidth=2)
    ax.grid(alpha=0.2)
    ax.set_ylabel('Average execution time (ms)')
    ax.set_xlabel('Input length')

fig, ax = plt.subplots(figsize=(7, 4.5))
for name in ['LCS', 'Edit Distance']:
    series(ax, name, 'random')
ax.set_title('Dynamic programming: two strings of equal length')
ax.legend()
fig.tight_layout()
fig.savefig(ROOT / 'results/dp_times.png')
plt.close(fig)
fig, axes = plt.subplots(1, 2, figsize=(11, 4.5), sharey=True)
for ax, scenario in zip(axes, ['present', 'absent']):
    for name in ['Naive', 'KMP', 'Rabin-Karp']:
        series(ax, name, scenario)
    ax.set_title(f'Pattern {scenario} (length 20)')
    ax.set_xlabel('Text length')
    ax.legend()
fig.tight_layout()
fig.savefig(ROOT / 'results/matching_times.png')
plt.close(fig)
fig, ax = plt.subplots(figsize=(7, 4.5))
series(ax, 'Kasai', 'random')
ax.set_title('Kasai: suffix-array construction excluded')
fig.tight_layout()
fig.savefig(ROOT / 'results/kasai_times.png')
plt.close(fig)
print('Saved three plots in results/.')