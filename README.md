# EnhanceRecyclerView

## 依赖

```
compile 'com.idisfkj.enchancerecyclerview:mylibrary:1.1.1'
```


# 使用

## xml中引用

```
<com.idisfkj.mylibrary.EnhanceRecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
</com.idisfkj.mylibrary.EnhanceRecyclerView>
```

## 设置监听

```
 mRecyclerView.setPullToRefreshListener(new com.idisfkj.mylibrary.EnhanceRecyclerView.PullToRefreshListener() {
            @Override
            public void onRefreshing() {
                refreshData();
            }
        });
        mRecyclerView.setLoadMoreListener(new EnhanceRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });
```

`refreshData()`与`loadMoreData()`加载数据的逻辑就不展示了，只是要记住在请求网络数据完之后要在他们中调用相应的`mRecyclerView.setRefreshComplete()`与` mRecyclerView.setLoadMoreComplete()`来重置状态。
至于其他的`Adapter`、`LayoutManager`等的设置就不多说了，与原生的`RecyclerView`是一样的。

# 效果

![效果图](https://github.com/idisfkj/EnhanceRecyclerView/raw/master/images/1.gif)

# 详细说明

[RecyclerView下拉刷新与上拉更多](http://idisfkj.github.io/2016/08/03/RecyclerView%E4%B8%8B%E6%8B%89%E5%88%B7%E6%96%B0%E4%B8%8E%E4%B8%8A%E6%8B%89%E6%9B%B4%E5%A4%9A/)

# License

```
Copyright (c) 2016. The Android Open Source Project
Created by idisfkj
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```