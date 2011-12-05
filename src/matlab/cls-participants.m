becks = [0.58 0.33 0.26 0.25 0.09]
farmerd = [0.79 0.60 0.37 0.34 0.29]
kaminskiv = [0.62 0.24 0.41 0.14 0.10]
kitchenl = [0.59 0.28 0.26 0.19 0.15]
lokaym = [0.86 0.79 0.48 0.15 0.11]
sandersr = [0.85 0.54 0.54 0.41 0.35]
williamsw3 = [0.97 0.86 0.63 0.78 0.37]
insticc = [0.61 0.40 0.44 0.31 0.26]


x = [becks; farmerd; kaminskiv; kitchenl; lokaym; sandersr; williamsw3; insticc]
bar(x)

xlabel('Data sets grouped by technique');
ylabel('Classifier accuracy');

xtick = {'beck-s', 'farmer-d', 'kaminski-v', 'kitchen-l', 'lokay-m', 'sanders-r', 'williams-w3', 'INSTICC'}
set(gca, 'XTickLabel', xtick); 

legend('Participants SVMs', 'Peoplefier', 'Peoplefier+SVMs', 'TM body', 'TM subject')
