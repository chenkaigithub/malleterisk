becks = [.60 .72]
farmerd = [.79 .81]
kaminskiv = [.70 .72]
kitchenl = [.54 .86]
lokaym = [.84 .85]
sandersr = [.79 .80]
williamsw3 = [.88 .94]
insticc = [.78 .88]

x = [becks; farmerd; kaminskiv; kitchenl; lokaym; sandersr; williamsw3; insticc]
bar(x)

xlabel('Data sets grouped by technique');
ylabel('Classifier accuracy');

xtick = {'beck-s', 'farmer-d', 'kaminski-v', 'kitchen-l', 'lokay-m', 'sanders-r', 'williams-w3', 'INSTICC'}
set(gca, 'XTickLabel', xtick); 

legend('Naive Bayes', 'Maximum Entropy')
