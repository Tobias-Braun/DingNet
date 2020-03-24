import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import pandas as ps
from time import sleep

rewards_1 = ps.read_csv('rewards-exp1.csv').to_numpy()
rewards_2 = ps.read_csv('rewards-exp2.csv').to_numpy()
rewards_3 = ps.read_csv('rewards-exp3.csv').to_numpy()
rewards_4 = ps.read_csv('rewards-exp4.csv').to_numpy()
rewards_5 = ps.read_csv('rewards-exp5.csv').to_numpy()
rewards_6 = ps.read_csv('rewards-exp6.csv').to_numpy()
rewards_7 = ps.read_csv('rewards-exp7.csv').to_numpy()
rewards_current = ps.read_csv('rewards-ppo-tp.csv').to_numpy()
indexes = np.arange(0, 15992, 8)

slice_1 = [rewards_1[i:i+8, 1] for i in indexes]
slice_1 = [[i for i in j] for j in slice_1]
avg_1 = [np.average(i) for i in slice_1]

slice_2 = [rewards_2[i:i+8, 1] for i in indexes]
slice_2 = [[i for i in j] for j in slice_2]
avg_2 = [np.average(i) for i in slice_2]

slice_3 = [rewards_3[i:i+8, 1] for i in indexes]
slice_3 = [[i for i in j] for j in slice_3]
avg_3 = [np.average(i) for i in slice_3]

slice_4 = [rewards_4[i:i+8, 1] for i in indexes]
slice_4 = [[i for i in j] for j in slice_4]
avg_4 = [np.average(i) for i in slice_4]

slice_5 = [rewards_5[i:i+8, 1] for i in indexes]
slice_5 = [[i for i in j] for j in slice_5]
avg_5 = [np.average(i) for i in slice_5]

slice_6 = [rewards_6[i:i+8, 1] for i in indexes]
slice_6 = [[i for i in j] for j in slice_6]
avg_6 = [np.average(i) for i in slice_6]

slice_7 = [rewards_7[i:i+8, 1] for i in indexes]
slice_7 = [[i for i in j] for j in slice_7]
avg_7 = [np.average(i) for i in slice_7]

slice_8 = [rewards_current[i:i+8, 1] for i in indexes]
slice_8 = [[i for i in j] for j in slice_8]
avg_8 = [np.average(i) for i in slice_8]


# print(slice_1)
fig = plt.figure(figsize=(8, 8))
ax = plt.subplot(111)
ax.set_xlabel('episode', fontsize=18)
ax.set_ylabel('cumulative reward', fontsize=18)
ax.plot(indexes, avg_1, color="b", lw=1, label='Experiment 1 - TP')
ax.plot(indexes, avg_2, color="r", lw=1, label='Experiment 2 - SF')
ax.plot(indexes, avg_3, color="g", lw=1, label='Experiment 3 - SR')
ax.plot(indexes, avg_4, color="darkviolet",
        lw=1, label='Experiment 4 - TP & SF')
ax.plot(indexes, avg_5, color="c", lw=1, label='Experiment 5 - TP & SR')
ax.plot(indexes, avg_6, color="darkorange",
        lw=1, label='Experiment 6 - SF & SR')
ax.plot(indexes, avg_7, color="grey", lw=1,
        label='Experiment 7 - TP & SF & SR')
ax.plot(indexes, avg_8, color="black", lw=1.5, label='Experiment 8 - PPO TP')
ax.grid()
ax.legend()
plt.title(
    'Cumulative Reward per Episode - Averaged in blocks of 8 episodes', fontsize=20)
plt.show()
