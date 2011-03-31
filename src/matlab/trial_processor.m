[file_name, path_name, filter_index] = uigetfile('*.*');

% load data
data = importdata([path_name file_name], ',');
instances = data.textdata(:, 1);

% format data (only retrieve specified columns)
real_class_indices = data.data(:, 1);

% retrieve best class indices
l0 = data.data(:, 3);
n_lines = size(l0,1);
best_class_indices = zeros(n_lines, 1);
l0 = data.data(:,3:end);
for i=1:n_lines
    l1 = l0(i, :);
    l2 = reshape(l1, 3, size(l1, 2)/3)';
    l3 = sortrows(l2, 3);
    l4 = l3(size(l3,1), 1);
    best_class_indices(i, 1) = l4;
end

% compute confusion matrix
cm = confusionmat(real_class_indices, best_class_indices);

% calculate class sizes
p = sum(cm, 2);

% normalize values
for i=1:length(p)
    cm(i,:) = cm(i,:) / p(i);
end

% plot horizontal graph bar, horizontal and vertical flip
h1 = subplot(2,1,1); bar(1:length(p), p); set(gca,'xlim',[0.5 (length(p)+0.5)]); 
% plot confusion matrix
h2 = subplot(2,1,2); imagesc(cm);
% reduce graph bar's with and adjust position
h1_pos = get(h1, 'pos');
h1_pos(4) = h1_pos(4) - 0.25;
h1_pos(2) = h1_pos(2) + h1_pos(4) + 0.20;
set(h1, 'pos', h1_pos);
% plot color bar
h3 = colorbar('SouthOutside');
% expand confusion matrix width and adjust position
h2_pos = get(h2, 'pos');
h2_pos(4) = 0.7;%h2_pos(4) + 0.25;
h2_pos(2) = 0.1;%h2_pos(2) + h2_pos(4);
set(h2, 'pos', h2_pos);
