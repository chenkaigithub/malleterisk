clear;
[file_name, path_name, filter_index] = uigetfile('*.*');

% load data
data = importdata([path_name file_name], ',');
instances = data.textdata(:, 1);

% format data (only retrieve specified columns)
real_class_indices = data.data(:, 1);
real_class_split = data.data(:, 3:4);

% retrieve best class indices
% values are not ordered
l0 = data.data(:, 5:end);
n_lines = size(l0,1);
best_class_indices = zeros(n_lines, 1);
for i=1:n_lines
    % build triplets (class_idx, class_label, class_value)
    l1 = l0(i, :);
    l2 = reshape(l1, 3, size(l1, 2)/3)';
    % sort by value and get first triplet's class_idx
    l3 = sortrows(l2, 3);
    l4 = l3(size(l3,1), 1);
    best_class_indices(i, 1) = l4;
end

% compute confusion matrix
[cm, order] = confusionmat(real_class_indices, best_class_indices);

% calculate class sizes
rc = unique([real_class_indices real_class_split], 'rows');
sizes = zeros(length(order), 2);
for i=1:length(rc)
    idx = find(order == rc(i, 1));
    sizes(idx, :) = rc(i, 2:3);
end
p = [sizes(:, 1) sizes(:, 2)]; % training documents size; test documents size
%p = [sum(cm,2) sum(cm,2)];
p_x = 1:length(p);

% normalize values by number of test documents
for i=p_x
    cm(i,:) = cm(i,:) / p(i, 2);
end

% plot horizontal graph bar, horizontal and vertical flip
h1 = subplot(2,1,1); bar(p_x, p, 'stacked'); set(gca,'xlim',[0.5 (length(p)+0.5)]); 
% set class labels (indices)
set(gca, 'XTick', p_x); set(gca, 'XTickLabel', order);
% plot confusion matrix
h2 = subplot(2,1,2); imagesc(cm);
% set class labels (indices)
set(gca, 'XTick', p_x); set(gca, 'XTickLabel', order);
% reduce graph bar's height and adjust position
h1_pos = get(h1, 'pos');
h1_pos(4) = h1_pos(4) - 0.25;
h1_pos(2) = h1_pos(2) + h1_pos(4) + 0.15;
set(h1, 'pos', h1_pos);
% plot color bar
colormap cool;
h3 = colorbar('SouthOutside');
% expand confusion matrix height and adjust position
h2_pos = get(h2, 'pos');
h2_pos(4) = 0.7;%h2_pos(4) + 0.25;
h2_pos(2) = 0.1;%h2_pos(2) + h2_pos(4);
set(h2, 'pos', h2_pos);
