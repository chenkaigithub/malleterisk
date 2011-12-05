becks = [.57 .56 .58 .58 .58 .58 .58 .57 .58]
farmerd = [.77 .76 .79 .79 .79 .77 .77 .77 .77]
kaminskiv = [.69 .68 .69 .69 .70 .69 .70 .69 .69]
kitchenl = [.55 .54 .55 .55 .55 .55 .55 .55 .55]
lokaym = [.82 .80 .82 .83 .83 .82 .82 .82 .81]
sandersr = [.77 .73 .80 .80 .81 .77 .77 .77 .77]
williamsw3 = [.87 .87 .9 .8 .89 .87 .87 .87 .87]
insticc = [.73 .72 .75 .76 .75 .73 .73 .73 .70]

x = [becks; farmerd; kaminskiv; kitchenl; lokaym; sandersr; williamsw3; insticc]
bar(x)

xlabel('Data sets grouped by technique');
ylabel('Classifier accuracy');

xtick = {'beck-s', 'farmer-d', 'kaminski-v', 'kitchen-l', 'lokay-m', 'sanders-r', 'williams-w3', 'INSTICC'}
set(gca, 'XTickLabel', xtick); 

legend('DF', 'Fish-MS', 'Fish-SS', 'Fish-SSS', 'IG', 'l0M1', 'l0M2', 'TW', 'Var')
