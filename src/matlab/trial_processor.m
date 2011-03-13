[file_name, path_name, filter_index] = uigetfile('*.*');

% load data
data = importdata([path_name file_name], ',');
instances = data.textdata(:, 1);

% format data (only retrieve specified columns)
real_class_indices = data.data(:, 2);
%real_class_names = data.data(:, 3);
best_class_indices = data.data(:, 4);
%best_class_names = data.data(:, 5);
%best_class_values = data.data(:, 6);

% compute confusion matrix
minx = min(real_class_indices);
maxx = max(real_class_indices);
c = zeros(maxx-minx); % this doesn't make much sense
% graph bar vector
p = zeros(1,length(unique(real_class_indices)));
counter = 1;
for i = minx:maxx
    % count real number of elements of class 'i'
    p(counter) = sum(real_class_indices==i);
    counter = counter + 1;
    
    % calculate confusion matrix
    index = find(real_class_indices == i);
    for j = minx:maxx
        z = best_class_indices(index);
        c(i-minx+1,j-minx+1) = length(find(z == j));
    end
end

% plot horizontal graph bar, horizontal and vertical flip
h1 = subplot(1,2,1); barh(p); set(gca,'xdir','reverse'); set(gca,'ydir','reverse');
% plot confusion matrix
h2 = subplot(1,2,2); imagesc(c); colorbar;
% reduce graph bar's with and adjust position
h1_pos = get(h1, 'pos');
h1_pos(3) = h1_pos(3) - 0.25;
h1_pos(1) = h1_pos(1) + h1_pos(3);
set(h1, 'pos', h1_pos);
% expand confusion matrix width and adjust position
h2_pos = get(h2, 'pos');
h2_pos(3) = h2_pos(3) + 0.25;
h2_pos(1) = h2_pos(1) - h2_pos(3)/2;
set(h2, 'pos', h2_pos);
