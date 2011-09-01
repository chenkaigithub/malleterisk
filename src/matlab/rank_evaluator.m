clear;
[file_name, path_name, filter_index] = uigetfile('*.*');

% load data
data = importdata([path_name file_name], ',');
instances = data.textdata(:, 1);

% format data (only retrieve specified columns)
real_class_indices = data.data(:, 1);

l0 = data.data(:, 5:end);
n_lines = size(l0,1);
best_class_indices = zeros(n_lines, 3); % the indices of the best classified classes
real_class_rank = zeros(n_lines, 1);    % the rank (position) of the real class
real_class_values = zeros(n_lines, 1);
for i=1:n_lines
    % build triplets (class_idx, class_label, class_value)
    l1 = l0(i, :);
    l2 = reshape(l1, 3, size(l1, 2)/3)';
    % sort by value and the last triplet's class_idx
    l3 = sortrows(l2, -3);
    best_class_indices(i, :) = l3(1, :);
    real_class_rank(i, 1) = find(l3(:, 1) == real_class_indices(i, 1));
    real_class_values(i, 1) = l3(real_class_rank(i,1), 3);
end

% best class, value | real class, value | rank of real class in classification
%horzcat(best_class_indices(:, 1), best_class_indices(:, 3), real_class_indices, real_class_values, real_class_rank)

table = horzcat(best_class_indices(:, 1), real_class_indices, real_class_rank)

sum(table(:, 3) < 5) / length(table)