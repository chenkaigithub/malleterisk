clear;
files = uipickfiles('FilterSpec', '/Work/msc/code/malleterisk-test/results');
num_files = length(files);

plots = zeros(1, num_files);
names = cell(1, num_files);
markers = ['.' 'o' 'x' '+' '*' 's' 'd' 'v' '^' '<' '>' 'p' 'h'];
num_markers = length(markers);
colors = [0.6 0.6 0.6; 0.3 0.3 0.3; 0 0.6 0.6; 0.8 0.6 0.2; 0 0 1; 0 1 0; 1 0 0; 1 0 1; 0.8 0.8 0; 0 1 1; 0 0 0];
num_colors = length(colors);
figure
m_sum = zeros(num_files, 2);
for i = 1:num_files
    indices = find(files{i} == '/');
    file_name = files{i}(indices(length(indices))+1:end);
    
    % load file into [feature, runs] matrix
    R = importdata([files{i}], ',');

    % calculate the mean of the runs
    l = size(R, 1);
    m = zeros(l,2);
    for j = 1:l
        r = R(j, 2:end);
        m(j,1) = mean(r);
        m(j,2) = (max(r) - min(r))/2;
    end
    m_sum(i, 1) = sum(m(:, 1)) / size(m, 1);
    m_sum(i, 2) = sum(m(:, 2)) / size(m, 1);
    
    % create new [feature, runs-mean] matrix
    M = [ R(:,1), m ]';
    M = sortrows(M.',1).';

    marker_idx = i;
    if marker_idx > num_markers
        marker_idx = uint8(i/num_markers);
    end

    color_idx = i;
    if color_idx > num_colors
       color_idx = uint8(i/num_colors);
    end

    %plots(i) = plot(M(1,:), M(2,:), 'color', rand(1,3), 'marker', markers(marker_idx));
    plots(i) = errorbar(M(1,:)+(i*20), M(2,:), M(3,:), 'color', colors(color_idx, :), 'marker', markers(marker_idx));%, 'linewidth', i*0.25); 
    names{i} = file_name;
    hold on;
end
if plots(1,1) == 0
    plots = plots(1,2:end);
    %names = names(1,2:end);
end
m_sum
%names = {'','','','','','','','',''};
legend(plots(1,1:end), names{1,1:end});
xlabel('Number of features');
ylabel('Classifier accuracy (%)');

