clear;
[file_name, path_name, filter_index] = uigetfile('*.*');

% load data
data = importdata([path_name file_name], ',');

% # instances / class
classes = data(:, 1);
num_instances = data(:, 2);
bar(classes, num_instances);

ann_obj = annotation('textbox', [.0, .7, .3, .3], 'String', 'Hello World!');


min(num_instances)
max(num_instances)

% instances
% classes
% avg instances/class
% largest class
% smallest class

% time period for classes
% participants in classes