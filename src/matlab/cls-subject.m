becks = [.52 .18 .55 .56 .53 .45]
farmerd = [.73 .51 .75 .76 .71 .60]
kaminskiv = [.55 .19 .57 .58 .55 .42]
kitchenl = [.48 .25 .5 .51 .49 .37]
lokaym = [.71 .53 .72 .75 .73 .65]
sandersr = [.68 .44 .71 .71 .71 .6]
williamsw3 = [.89 .89 .9 .92 .83 .86]
insticc = [.80 .54 .81 .82 .77 .66]

x = [becks; farmerd; kaminskiv; kitchenl; lokaym; sandersr; williamsw3; insticc]
bar(x)

xlabel('Data sets grouped by technique');
ylabel('Classifier accuracy');

xtick = {'beck-s', 'farmer-d', 'kaminski-v', 'kitchen-l', 'lokay-m', 'sanders-r', 'williams-w3', 'INSTICC'}
set(gca, 'XTickLabel', xtick); 

legend('Balanced Winnow', 'Decision Tree', 'SVMs', 'Max Entropy', 'Naive Bayes', 'Winnow')
