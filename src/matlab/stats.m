clear;
[file_name, path_name, filter_index] = uigetfile('*.*');

% load data
data = importdata([path_name file_name], ',');

% # instances / class
classes = data(:, 1);
num_instances_per_class = data(:, 2);
bar(classes, num_instances_per_class);

n_insts = sum(num_instances_per_class);
n_class = length(classes);
min_n_inst = min(num_instances_per_class);
max_n_inst = max(num_instances_per_class);
avg_n_inst_n_class = sum(num_instances_per_class) / length(classes);

ann_obj = annotation('textbox', [.135, .77, .23, .15] , 'String', ...
    strcat('Statistics:', ...
    '\newlineNumber of classes: ', num2str(n_class), ...
    '\newlineNumber of instances: ', num2str(n_insts), ...
    '\newlineSmallest class: ', num2str(min_n_inst), ...
    '\newlineLargest class: ', num2str(max_n_inst), ...
    '\newlineAverage number of instances per class: ', num2str(avg_n_inst_n_class)), ...
    'FontSize', 8);
