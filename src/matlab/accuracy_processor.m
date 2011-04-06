clear;
dir_name = uigetdir;
%dir_name = uigetdir('../../results/2011-04-04/accuracies/');
%dir_name = uigetdir('C:\Work\tt\seamce-test\results\2011-04-05-fs');
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+1\u1+1+boolean';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+1\u1+1+tf-idf';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+1\u1+1+tf-log';

% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+2\u1+2+boolean';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+2\u1+2+tf-idf';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+2\u1+2+tf-log';
% 
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+3\u1+3+boolean';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+3\u1+3+tf-idf';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+3\u1+3+tf-log';
% 
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+4\u1+4+boolean';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+4\u1+4+tf-idf';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+4\u1+4+tf-log';
% 
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+5\u1+5+boolean';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+5\u1+5+tf-idf';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+5\u1+5+tf-log';
% 
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+6\u1+6+boolean';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+6\u1+6+tf-idf';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+6\u1+6+tf-log';
% 
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+7\u1+7+boolean';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+7\u1+7+tf-idf';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u1+7\u1+7+tf-log';
% 
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u2+1\u2+1+boolean';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u2+1\u2+1+tf-idf';
% dir_name = 'C:\Work\tt\seamce-test\results\2011-04-05-fs\accuracies\u2+1\u2+1+tf-log';

files = dir(dir_name);
file_indices = find(~[files.isdir]);
num_files = length(file_indices);

plots = zeros(1, num_files);
names = cell(1, num_files);
markers = ['.' 'o' 'x' '+' '*' 's' 'd' 'v' '^' '<' '>' 'p' 'h'];
num_markers = length(markers);
colors = [0.6 0.6 0.6; 0.3 0.3 0.3; 0 0.6 0.6; 0.8 0.6 0.2; 0 0 1; 0 1 0; 1 0 0; 1 0 1; 0.8 0.8 0; 0 1 1; 0 0 0];
num_colors = length(colors);
figure
for i = 1:num_files
    file_name = files(file_indices(i)).name;
    if ~strcmp(file_name, '.DS_Store')
        % load file into [feature, runs] matrix
        R = load([dir_name '/' file_name]);
        
        % calculate the mean of the runs
        l = size(R, 1);
        m = zeros(l,2);
        for j = 1:l
            r = R(j, 2:end);
            m(j,1) = mean(r);
            m(j,2) = (max(r) - min(r))/2;
        end
        
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
end
if plots(1,1) == 0
    plots = plots(1,2:end);
    names = names(1,2:end);
end
legend(plots(1,1:end), names{1,1:end});
title(dir_name);


