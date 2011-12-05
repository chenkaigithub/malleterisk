becks = [0.90  0.93  0.93]
farmerd = [0.96  0.97  0.98]
kaminskiv = [0.90  0.95  0.98]
kitchenl = [0.95  0.96  0.97]
lokaym = [0.93  0.99  0.99]
sandersr = [0.97  0.99  0.99]
williamsw3 = [0.99  1.00  1.00]
insticc = [0.91  0.98  0.99]


x = [becks; farmerd; kaminskiv; kitchenl; lokaym; sandersr; williamsw3; insticc]
bar(x)

xlabel('Data sets grouped by technique');
ylabel('Classifier accuracy');

xtick = {'beck-s', 'farmer-d', 'kaminski-v', 'kitchen-l', 'lokay-m', 'sanders-r', 'williams-w3', 'INSTICC'}
set(gca, 'XTickLabel', xtick); 

legend('10%', '60%', '100%')
