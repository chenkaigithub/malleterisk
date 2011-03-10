dir_name = uigetdir;
files = dir(dir_name);
file_indices = find(~[files.isdir]);
num_files = length(file_indices);

plots = zeros(1, num_files);
names = cell(1, num_files);
markers = ['.' 'o' 'x' '+' '*' 's' 'd' 'v' '^' '<' '>' 'p' 'h'];
num_markers = length(markers);
for i = 1:num_files
    file_name = files(file_indices(i)).name;
    if ~strcmp(file_name, '.DS_Store')
        % load file into [feature, runs] matrix
        R = load([dir_name '/' file_name]);
        
        % calculate the mean of the runs
        l = length(R);
        m = zeros(l,2);
        for j = 1:l
            r = R(j, 2:end);
            m(j,1) = mean(r);
            m(j,2) = (max(r) - min(r))/2;
        end
        
        % create new [feature, runs-mean] matrix
        M = [ R(:,1), m ]';

        marker_idx = i;
        if marker_idx > num_markers
            marker_idx = uint8(i/length(markers));
        end
        
        plots(i) = plot(M(1,:), M(2,:), 'color', rand(1,3), 'marker', markers(marker_idx));
        %plots(i) = errorbar(M(1,:)+(i*20), M(2,:), M(3,:), 'color', rand(1,3), 'marker', markers(marker_idx)); 
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


