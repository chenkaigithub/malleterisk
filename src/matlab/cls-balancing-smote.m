becks = [0.82  0.90  0.91]
farmerd = [0.90  0.93  0.94]
kaminskiv = [0.82  0.90  0.92]
kitchenl = [0.95  0.96  0.96]
lokaym = [0.87  0.93  0.95]
sandersr = [0.93  0.96  0.97]
williamsw3 = [0.96  0.98  0.99]
insticc = [0.86  0.91  0.93]


x = [becks; farmerd; kaminskiv; kitchenl; lokaym; sandersr; williamsw3; insticc]
bar(x)

xlabel('Data sets grouped by technique');
ylabel('Classifier accuracy');

xtick = {'beck-s', 'farmer-d', 'kaminski-v', 'kitchen-l', 'lokay-m', 'sanders-r', 'williams-w3', 'INSTICC'}
set(gca, 'XTickLabel', xtick); 

legend('10%', '60%', '100%')
