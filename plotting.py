import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import pandas as ps
from time import sleep

data = ps.read_csv('max_values.csv').to_numpy()
ep_ends = np.where(data[:,0] == 0)[0]
index = 3000
show_tp = True
show_sf = True
show_sr = True
# Create new Figure with black background
fig = plt.figure(figsize=(8, 8))
ax = None
ax2 = None
ax3 = None
line = None
line2 = None
line3 = None
label_size = 19

# Add a subplot with no frame
if show_tp:
    ax = plt.subplot(111, frameon=True)
    # ax.grid()
    ax.set_ylim(-170,-110)
    ax.legend(['transmission power'])
    ax.set_ylabel('transmission power', fontsize=label_size)

if show_sf:
    if not show_tp:
        ax2 = plt.subplot(111, frameon=True)
        # ax2.grid()
        ax = ax2
    else: ax2 = ax.twinx()
    ax2.set_ylim(6,13)
    ax2.legend('spreading factor')
    ax2.set_ylabel('spreading factor', fontsize=label_size)
if show_sr:
    if not show_tp and not show_sf:
        ax3 = plt.subplot(111, frameon=True)
        # ax3.grid()
        ax = ax3
    else: ax3 = ax.twinx()
    ax3.legend('sampling rate')
    ax3.set_ylabel('sampling rate', fontsize=label_size)
    ax3.spines["right"].set_position(("axes", 1.06))
    ax3.set_ylim(0,16)
    ax.set_xlim(0,500)

plt.grid()
if index+1 < 16000:
    subdata = data[ep_ends[index]: ep_ends[index+1]]
else:
    subdata = data[ep_ends[index]: len(data)]


ax.set_xlabel('state number in episode', fontsize=label_size)
if show_tp:
    line, = ax.plot(subdata[:,0], subdata[:,5], color="b", lw=1, label=f'Transmission power')
if show_sf:
    line2, = ax2.plot(subdata[:,0], subdata[:,4], color="r", lw=1, label=f'Spreading factor')
if show_sr:
    line3, = ax3.plot(subdata[:,0], subdata[:,3], color="g", lw=1, label=f'Sampling rate')

plt.legend((line, line2, line3), ('Transmission power', 'Spreading factor', 'Sampling rate'))
def update(*args):
    # Get subdata
    global index
    global show_sf
    global show_tp
    global show_sr
    if index is 1:
        plt.pause(5)
    plt.title(f'Experiment 3 Results - Episode {index+1}', fontsize=20)
    if index < len(ep_ends) -1:
        index += 1 + index//80
        if (index >= len(ep_ends)-1):
            index = len(ep_ends) - 1
    print(index)
    if index+1 < 16000:
        subdata = data[ep_ends[index]: ep_ends[index+1]]
    else:
        subdata = data[ep_ends[index]: len(data)]
    # Update data
    if show_tp:
        line.set_data(subdata[:,0], subdata[:,5])
    if show_sf:
        line2.set_data(subdata[:,0], subdata[:,4])
    if show_sr:
        line3.set_data(subdata[:,0], subdata[:,3])
    # Return modified artists
    return line

# Construct the animation, using the update function as the animation director.
anim = animation.FuncAnimation(fig, update, interval=60)
plt.show()
