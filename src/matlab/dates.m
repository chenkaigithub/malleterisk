clear;
[file_name, path_name, filter_index] = uigetfile('*.*');

data = importdata([path_name file_name], ',');

[y,I]=sortrows(data, [1,2]);
minYer=y(1,1);
%minMon=y(1,2);
maxYer=y(end,1);
%maxMon=y(end,2);

columns = unique(y(:, 3));

m=[];
count=0;
for i=minYer:maxYer
    [Iy]=find(y(:,1)==i);
    for j=1:12
        [Im]=find(y(Iy,2)==j);
        if ~isempty(Im)
            count=count+1;
            s{count}=[num2str(j) '/' num2str(i)];
            classes = y(Iy(Im), 3);
            values = y(Iy(Im),4);
            for k=1:length(classes)
                m(find(columns == classes(k)), count)=values(k);
            end
        end
    end
end

imagesc(m)
%colormap(lines)
colorbar

set(gca, 'XTick', 1:size(s, 2)); 
set(gca, 'XTickLabel', s);
xticklabel_rotate();
%set(gca, 'YTick', 1:(size(Iy, 1)-1));

xlabel('Time');
ylabel('Class');
