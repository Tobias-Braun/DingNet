import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import pandas as ps
import seaborn as sns

sns.set()
data = ps.read_csv('max_values.csv').to_numpy()
ep_ends = np.where(data[:,0] == 0)[0]
index = 0
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


# Add a subplot with no frame
if show_tp:
    ax = plt.subplot(111, frameon=False)
    ax.grid()
    ax.set_ylim(-160,-120)
    ax.legend(['transmission power'])
    ax.set_ylabel('transmission power')
    ax.set_xlabel('number of transmissions sent')

if show_sf:
    if not show_tp:
        ax2.grid()
    ax2 = ax.twinx()
    ax2.set_ylim(6,13)
    ax2.legend('spreading factor')
    ax2.set_ylabel('spreading factor')
if show_sr:
    if not show_tp and not show_sf:
        ax3.grid()
    ax3 = ax.twinx()
    ax3.legend('sampling rate')
    ax3.set_ylabel('sampling rate')
    ax3.spines["right"].set_position(("axes", 1.06))
    ax3.set_ylim(0,16)
subdata = data[ep_ends[index]: ep_ends[index+1] if len(ep_ends) >= index else len(data)]
line, = ax.plot(subdata[:,0], subdata[:,5], color="b", lw=1, label=f'Episode {index}')
if show_sf:
    line2, = ax2.plot(subdata[:,0], subdata[:,4], color="r", lw=1, label=f'Episode {index}')
if show_sr:
    line3, = ax3.plot(subdata[:,0], subdata[:,3], color="g", lw=1, label=f'Episode {index}')

def update(*args):
    # Get subdata
    global index
    global show_sf
    global show_sf
    if index < len(ep_ends) -1:
        index += (index//1000) + 2
        if (index >= len(ep_ends)-1):
            index = len(ep_ends) - 2
    print(index)
    subdata = data[ep_ends[index]: ep_ends[index+1] if index < len(ep_ends)-1 else len(data)]
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
anim = animation.FuncAnimation(fig, update, interval=4)
plt.show()
