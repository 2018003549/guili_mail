# 仿京东商城电商平台

### 首页

#### 动静分离

- 静态资源放在nginx
- 动态资源就去请求各个服务

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121161321658.png" alt="image-20231121161321658" style="zoom:67%;" />.

#### 环境搭建

1. 导入thymeleaf模板引擎和devtools【ctrl+f9就可以动态刷新页面】

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

2. 关闭thymeleaf的缓存【关闭缓存才不会影响动态刷新】

   ```java
   thymeleaf:
     cache: false
   ```

3. 导入静态资源和页面

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121162135700.png" alt="image-20231121162135700" style="zoom:67%;" />.

4. 将页面所有路径的static去掉【因为访问静态资源是从static目录开始找的】

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121164926482.png" alt="image-20231121164926482" style="zoom: 50%;" />.

#### 动态显示三级分类

1. 引入thymeleaf名称空间`<html lang="en" xmlns:th="http://www.thymeleaf.org">`

2. 查询一级分类

   ```java
   @GetMapping({"/","index.html"})
   public String indexPage(Model model){
       //查出所有一级分类
       List<CategoryEntity> entityList=categoryService.getLevelFirst();
       model.addAttribute("categorys",entityList);
       return "index";
   }
   @Override
   public List<CategoryEntity> getLevelFirst() {
       List<CategoryEntity> categoryEntities = baseMapper.selectList(
               new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
       return categoryEntities;
   }
   ```

3. 光标移动到一级分类就显示响应二三级分类

   1. 根据前端要求的数据格式封装数据

      ```java
      @AllArgsConstructor
      @NoArgsConstructor
      @Data
      public class Catelog2Vo {
          private String catalog1Id;//1级父分类id
          private List<Catelog3Vo> catalog3List;//3级子分类id
          private String id;
          private String name;
          @AllArgsConstructor
          @NoArgsConstructor
          @Data
          public static class Catelog3Vo{
              private String catalog2Id;//2级父分类id
              private String id;
              private String name;
          }
      }
      ```

   2. 修改三级分类发送请求路径`$.getJSON("index/catalog.json",function (data) `

   3. 封装二三级分类

      ```java
      public Map<String, List<Catelog2Vo>> getCatelogJson() {
          //1.查出所有一级分类
          List<CategoryEntity> levelFirst = getLevelFirst();
          //封装数据
          Map<String, List<Catelog2Vo>> map = levelFirst.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
              //2.查询每个一级分类的所有二级分类
              List<CategoryEntity> categoryEntities = baseMapper.selectList(
                      new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
              List<Catelog2Vo> catelog2VoList = null;
              if (categoryEntities != null) {
                  catelog2VoList = categoryEntities.stream().map(level2 -> {
                      Catelog2Vo catelog2Vo = new Catelog2Vo
                              (v.getCatId().toString(), null, level2.getCatId().toString(), level2.getName());
                      //3.查询当前二级分类的三级分类
                      List<CategoryEntity> catelog3List = baseMapper.selectList(
                              new QueryWrapper<CategoryEntity>().eq("parent_cid", level2.getCatId()));
                      if(catelog3List!=null){
                          //封装成指定格式
                          List<Catelog2Vo.Catelog3Vo> catelog3VoList = catelog3List.stream().map(level3-> {
                              Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo
                                      (level2.getCatId().toString(),level3.getCatId().toString(),level3.getName());
                              return catelog3Vo;
                          }).collect(Collectors.toList());
                          catelog2Vo.setCatalog3List(catelog3VoList);
                      }
                      return catelog2Vo;
                  }).collect(Collectors.toList());
              }
              return catelog2VoList;
          }));
          return map;
      }
      ```

   4. 测试【ctrl+f5刷新页面缓存】

      <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121175041856.png" alt="image-20231121175041856" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121175430087.png" alt="image-20231121175430087" style="zoom:67%;" />.

#### nginx搭建域名环境

##### windows修改本地域名映射规则

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121195158383.png" alt="image-20231121195158383" style="zoom:67%;" />.

1. SwitchHosts软件中新增hosts方案，配置域名映射【要以管理员身份运行】

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121202321032.png" alt="image-20231121202321032" style="zoom:67%;" />.

2. 测试

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121195547995.png" alt="image-20231121195547995" style="zoom:67%;" />.

##### 使用nginx进行反向代理

- 需求：所有来自于gulimall.com的请求都转到商品服务
- 本机发送gulimall.com域名映射到虚拟机，被nginx接收，然后再转发给本机的商品服务

1. 配置gulimall.com映射到主机商品服务

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121201420615.png" alt="image-20231121201420615" style="zoom:67%;" />.

2. 重启有问题，gulimall.conf的第十行配置出错，原因是nginx配置**必须以分号结尾**

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121201615696.png" alt="image-20231121201615696" style="zoom:67%;" />![image-20231121201747280](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121201747280.png)

3. 访问成功

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121201929524.png" alt="image-20231121201929524" style="zoom:67%;" />.

##### 配置nginx负载均衡

- 过程梳理：访问gulimall.com域名==》windows根据hosts中的映射关系将请求转发到虚拟机==》请求会被nignx的一个server监听==》然后代理给windows主机的网关【会丢失host等信息，需要配置】==》最后网关进行路由映射

1. 在nginx.conf中配置上游服务器

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122132252226.png" alt="image-20231122132252226" style="zoom:67%;" />.

2. server块中配置代理规则，路由到gulimall的上游服务器组

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122132427051.png" alt="image-20231122132427051" style="zoom:67%;" />.

3. 在网关中配置主机路由规则

   - **表示任意的子域名
   - 该配置只能放在最后边，否则会拦截商品服务的精准请求

   ```yaml
   - id: gulimall_host_route
     uri:  lb://gulimall-product
     predicates:
       - Host=**.gulimall.com
   ```

4. nginx可以路由到网关，具体方法可以映射，但是域名无法直接映射到商品服务

   - 原因是nginx代理给网关时**会丢失host的域名信息等等**

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122134711890.png" alt="image-20231122134711890" style="zoom: 50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122134656510.png" alt="image-20231122134656510" style="zoom:67%;" />

   - 配置nignx映射规则`proxy_set_header Host $host;`

     <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122135233556.png" alt="image-20231122135233556" style="zoom:67%;" />.

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122135419765.png" alt="image-20231122135419765" style="zoom:67%;" />.

### 检索服务【boss3，前端是坑】

#### 环境搭建

1. 将检索服务相关的静态资源放入nignx的html的search目录下

2. 检索服务引入模板引擎和dev-tools依赖

3. 项目导入页面

   - 页面要引入thymeleaf的名称空间
   - 修改所有的静态资源引用路径

4. 测试

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127100548766.png" alt="image-20231127100548766" style="zoom: 50%;" />.

5. 配置域名转发

   - windows配置<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127100912719.png" alt="image-20231127100912719" style="zoom:67%;" />

   - nginx配置，接收所有域名中带有gulimall.com的请求<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127101014865.png" alt="image-20231127101014865" style="zoom:67%;" />

   - 网关配置

     - 修改之前的配置，不将所有域名带有gulimall.com的请求转发给商品服务，而是完全匹配gulimall.com才转发给商品服务
     - 域名精准匹配`search.gulimall.com`就转发给检索服务

     ```yaml
     - id: gulimall_host_route
       uri:  lb://gulimall-product
       predicates:
         - Host=gulimall.com
     - id: gulimall_search_route
       uri: lb://gulimall-search
       predicates:
         - Host=search.gulimall.com
     ```

6. 测试

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127101527382.png" alt="image-20231127101527382" style="zoom: 50%;" />.

#### 调整页面跳转

1. 返回首页

   ```html
   <a href="http://gulimall.com" class="header_head_p_a1" style="width:73px;">
   <a href="http://gulimall.com"><img src="/static/search/image/logo1.jpg" alt=""></a>
   ```

2. 域名无法正确处理，原因是nginx中的域名配置的*.gulimall.com是**不包含纯gulimall.com的情况**

   - 配置改成`gulimall.com  *.gulimall.com`

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127103644378.png" alt="image-20231127103644378" style="zoom:67%;" />.

3. 通过点击分类进到检索服务

   1. 将检索服务的index.html改成list.html

   2. 控制器方法

      ```java
      @GetMapping("/list.html")
      public String listPage(){
          return "list";
      }
      ```

   3. 测试，发送的域名不对劲

      <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127111122510.png" alt="image-20231127111122510" style="zoom:67%;" />.

      - 原因是catalogLoader.js的方法挖坑<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127111241517.png" alt="image-20231127111241517" style="zoom:67%;" />
      - 解决：把超链接的gmall.com改成gulimall.com即可<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127111427738.png" alt="image-20231127111427738" style="zoom:67%;" />

      <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127111514690.png" alt="image-20231127111514690" style="zoom: 50%;" />.

4. 点击搜索按钮跳转到检索服务，修改搜索按钮绑定的方法的超链接

   ```js
   function search() {
       var keyword=$("#searchText").val()
       window.location.href="http://search.gulimall.com/list.html?keyword="+keyword;
   }
   ```

#### 检索查询分析

##### 检索参数分析

- 商品检索三个入口：选择分类进入商品检索、输入检索关键字展示检索页、选择筛选条件进入

- 检索条件&排序条件

  - 全文检索：skuTitle-》keyword
  - 排序
    - saleCount（销量）、hotScore（热度分）、skuPrice（价格）
    - 排序字符串命名规则：排序字段名称_asc/desc
  - 过滤
    - hasStock
    - skuPrice区间【`1_500`表示1-500、`_500`表示500以下、`500_`表示500以上】
    - brandId，允许指定多个品牌
    - catalog3Id
    - attrs ，允许指定多个不同的属性，属性用字符串存储
      - 同一个属性的多个不同值用`:`分隔
      - 属性id放在字符串最开始，和属性值用`_`分隔
  - 聚合：attrs

- 完整查询参数【后面的查询参数是基于前面的查询结果的】

  ```url
  keyword=小米&sort=saleCount_desc/asc&hasStock=0/1
  &skuPrice=400_1900
  &brandId=1&brandId=2&catalog3Id=1
  &attrs=1_3G:4G:5G&attrs=2_骁龙845&attrs=4_高清屏
  ```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127125955302.png" alt="image-20231127125955302" style="zoom: 50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127130009356.png" alt="image-20231127130009356" style="zoom:50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127130023744.png" alt="image-20231127130023744" style="zoom: 50%;" />

```java
@Data
public class SearchParam {
    private  String keyword;//全文匹配关键字
    private Long catalog3Id;//三级分类id
    /**
     * sort=saleCount_asc/dese
     * sort=skuPrice_asc/dese
     * sort=hotScore_asc/dese
     */
    private String sort;//排序条件
    //过滤条件
    private Integer hasStock;//是否有库存，0无1有
    private String skuPrice;//价格区间，规则为【1_500表示1-500、_500表示500以下、500_表示500以上】
    private List<Long> brandId;//指定品牌
    private List<String> attrs;//按照属性进行筛选
    private Integer pageNum=1;//页码，默认从第一页开始
}
```

##### 返回结果分析

```java
@Data
public class SearchResult {
    private List<SkuEsModel> products;//查询到的所有商品信息
    //分页信息
    private Integer pageNum;//当前页码
    private Long total;//总记录数
    private Integer totalPages;//总页码
    private List<BrandVo> brands;//当前查询到的结果涉及的所有品牌
    private List<AttrVo> attrs;//当前查询到的结果涉及的所有属性
    private List<CatalogrVo> catalogs;//当前查询到的结果涉及的所有分类
    //=================以上是返回给页面的所有信息====================
    //品牌信息
    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }
    //属性信息
    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
    //分类信息
    @Data
    public static class CatalogrVo {
        private Long catalogId;
        private String catalogName;
    }
}
```

##### 检索dsl构建

###### 查询规则

- 使用bool组合多个检索条件，规定

  - 全文检索放在must中

  ```json
  "must": [
  {
    "match": {
      "skuTitle": "华为"
    }
  }]
  ```

  - 其它检索条件放在filter中【不参与评分会更快】
  - 数组可以使用terms

  ```java
  "terms": {
      "brandId": [
        "1","2","9"
  ]}
  ```

- attrs字段是nested类型的，不能直接通过字段名.属性名来访问，需要使用嵌入式查询

  - 嵌入式属性查询、聚合、分析都需要用嵌入式的
  - path表示嵌入式的字段

  ```json
  "nested": {
    "path": "attrs",
    "query": {
      "bool": {
        "must": [
          {
            "term": {
              "attrs.attrId": {
                "value": "15"
              }
            }
          },
          {
            "terms": {
              "attrs.attrValue": [
                "高通(Qualcomm)",
                "HUAWEI Kirin 980"
              ]}
          }
        ]
      }
    }
  }
  ```

###### 查询dsl

```json
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "skuTitle": "华为"
          }
        }
      ],
      "filter": [
        {
          "term": {
            "catalogId": "225"
          }
        },
        {
          "terms": {
            "brandId": [
              "1",
              "2",
              "9"
            ]
          }
        },
        {
          "nested": {
            "path": "attrs",
            "query": {
              "bool": {
                "must": [
                  {
                    "term": {
                      "attrs.attrId": {
                        "value": "15"
                      }
                    }
                  },
                  {
                    "terms": {
                      "attrs.attrValue": [
                        "高通(Qualcomm)",
                        "HUAWEI Kirin 980"
                      ]
                    }
                  }
                ]
              }
            }
          }
        },
        {
          "term": {
            "hasStock": {
              "value": "true"
            }
          }
        },
        {
          "range": {
            "skuPrice": {
              "gte": 0,
              "lte": 6000
            }
          }
        }
      ]
    }
  }
```

###### 其它dsl

- 排序+分页+高亮【将匹配的关键词高亮】
  - 高亮实际上是将指定字段中的匹配内容**加上前后缀**<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127172626311.png" alt="image-20231127172626311" style="zoom:67%;" />

```json
  "sort": [
    {
      "skuPrice": {
        "order": "desc"
      }
    }
  ],
  "from": 0,
  "size": 5,
  "highlight": {
    "fields": {
      "skuTitle": {}
    },
    "pre_tags": "<b style='color:red>",
    "post_tags": "</b>"
  }
```

###### 聚合分析

- 根据条件查询到的数据，之后新的检索条件也是动态变化的，所以需要聚合分析返回的结果

- 使用terms查询所有商品的品牌id和分类id的分布，并通过子聚合查询出它们的名称等相关信息

  - 报错：无法对brandName字段进行聚合，

    ```
    "Can't load fielddata on [brandName] because fielddata is unsupported on fields of type [keyword]. Use doc values instead."
    ```

  - 原因：设计mapping时，将brandName设置了`"doc_values" : false`，无法进行聚合操作

    ```json
    "brandImg": {
        "type": "keyword",
        "index": false,
        "doc_values": false
    },
    ```

  - 解决：先创建新索引指定新的映射规则【所有字段都不指定"index"和"doc_values"规则】，然后数据迁移【记得修改项目中的商品服务存储的索引常量】

    ```json
    POST _reindex
    {
        "source": {
        "index": "product"
      },
      "dest": {
        "index": "gulimall_product"
      }
    }
    public static final String PRODUCT_INDEX="gulimall_product";//sku数据在es中的索引
    ```

```json
GET gulimall_product/_search
{
  "query": {
    "match_all": {}
  },"aggs": {
    "brand_agg": {
      "terms": {
        "field": "brandId",
        "size": 10
      },
      "aggs": {
        "brand_name_agg": {
          "terms": {
            "field": "brandName",
            "size": 10
          }
        },
        "brand_img_agg":{
          "terms": {
            "field": "brandImg",
            "size": 10
          }
        }
      }
    },
    "catalog_agg":{
      "terms": {
        "field": "catalogId",
        "size": 10
      },"aggs": {
        "catalog_name_agg": {
          "terms": {
            "field": "catalogName",
            "size": 10
          }
        }
      }
    }
  }
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127174632462.png" alt="image-20231127174632462" style="zoom:67%;" />.

- 聚合嵌入式属性需要使用嵌入式的方式
  - 首先需要指定嵌入式的字段
  - 然后在同层指定聚合函数

```java
"attrs_agg":{
  "nested": {
    "path": "attrs"
  },
  "aggs": {
    "attr_id_agg": {
      "terms": {
        "field": "attrs.attrId",
        "size": 10
      },
      "aggs": {
        "attr_name_agg": {
          "terms": {
            "field": "attrs.attrName",
            "size": 10
          }
        },
        "attr_value_agg":{
          "terms": {
            "field": "attrs.attrValue",
            "size": 10
          }
        }
      }
    }
  }
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231127180147691.png" alt="image-20231127180147691" style="zoom:67%;" />.

#### 业务代码

##### 业务骨架

- 需要先创建MallSearchService接口和实现类【标识@Service】

```java
@Autowired
MallSearchService mallSearchService;
@GetMapping("/list.html")
public String listPage(SearchParam param, Model model){
    //根据传递来的查询参数，去es中检索商品
    SearchResult result=mallSearchService.search(param);
    model.addAttribute("result",result);
    return "list";
}
@Override
public SearchResult search(SearchParam param) {
    //1.动态构建查询需要的dsl语句
    	//1.1准备检索请求
    SearchRequest searchRequest = bulidSearchRequest(param);
    SearchResult result = null;
    try {
        //1.2执行检索请求
        SearchResponse response = client.search(searchRequest, EsConfig.COMMON_OPTIONS);
        //1.3分析响应数据，封装成需要的格式
        result=bulidSearchResult(response,param);
    } catch (IOException e) {
        e.printStackTrace();
    }
    return result;
}
```

##### 抽取准备检索请求

###### 构建查询条件

```java
SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//用于构建dsl语句
//1.构建查询功能
BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();//先构建bool query,用于组合多个查询条件
	//1.1.must-构建标题的模糊匹配
if(!StringUtils.isEmpty(param.getKeyword())){
    boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
}
	//1.2filter-不参与评分
		//1.2.1按照三级分类id查询
if(param.getCatalog3Id()!=null){
    boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
}
		//1.2.2按照多个品牌id查询
if(param.getBrandId()!=null&&param.getBrandId().size()>0){
    boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
}
		//1.2.3按照属性id进行查询【需要嵌入式处理】
if(param.getAttrs()!=null&&param.getAttrs().size()>0){
    for (String attrStr : param.getAttrs()) {
        //同一种类型的属性的多个不同值用‘:’分割，如attr=1.5寸:8寸,属性id和属性值用‘_’分割
        String[] split = attrStr.split("_");//先将属性id和属性值分隔开
        String attrId=split[0];
        String[] attrValues = split[1].split(":");//同一种属性可能有多个值
        BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
        nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
        nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
        //每一个属性都必须单独指定嵌入式查询，不同属性之间
        NestedQueryBuilder nestedQuery = QueryBuilders.
                nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);//ScoreMode指定按照什么参与评分
        boolQuery.filter(nestedQuery);
    }
}
		//1.2.4按照库存进行查询 0无库存 1有库存
if(param.getHasStock()!=null){
    boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
}
		//1.2.5查询指定价格区间
if(!StringUtils.isEmpty(param.getSkuPrice())){
    //【`1_500`表示1-500、`_500`表示500以下、`500_`表示500以上】
    RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
    String[] split = param.getSkuPrice().split("_");//按照‘_’进行分割
    if(split.length==2){
        //如果有两个值就说明指定了最大值和最小值
        rangeQuery.gte(split[0]).lte(split[1]);
    }else {
        if(param.getSkuPrice().startsWith("_")){
            rangeQuery.lte(split[0]);//只指定了上界
        }else {
            rangeQuery.gte(split[0]);//只指定了下界
        }
    }
    boolQuery.filter(rangeQuery);
}
//1.3封装所有的查询条件
sourceBuilder.query(boolQuery);
```

###### 构建排序、分页和高亮

```java
//2.1排序,排序字段命名规则：排序字段名称_asc/desc
if(!StringUtils.isEmpty(param.getSort())){
    String sort = param.getSort();
    String[] split = sort.split("_");
    SortOrder order=split[1].equalsIgnoreCase("asc")? SortOrder.ASC:SortOrder.DESC;
    sourceBuilder.sort(split[0],order);
}
//2.2分页
sourceBuilder.from((param.getPageNum()-1)*EsConstant.PRODUCT_PAGESIZE);
sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
//2.3高亮
if(!StringUtils.isEmpty(param.getKeyword())){
    //只有进行模糊匹配才需要标识高亮
    HighlightBuilder highlightBuilder = new HighlightBuilder();
    highlightBuilder.field("skuTitle");//需要高亮的字段
    highlightBuilder.preTags("<b style='color:red'>");
    highlightBuilder.postTags("</b>");
    sourceBuilder.highlighter(highlightBuilder);
}
```

###### 测试一

- 测试不带条件查询

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231128141959134.png" alt="image-20231128141959134" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231128142011223.png" alt="image-20231128142011223" style="zoom:67%;" />

  - 默认查询有库存的数据，查询到10条数据

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231128142042300.png" alt="image-20231128142042300" style="zoom:67%;" />.

- 测试全量条件

  - 全量条件

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231128143208956.png" alt="image-20231128143208956" style="zoom:67%;" />.

  - 测试结果

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231128143250722.png" alt="image-20231128143250722" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231128143301193.png" alt="image-20231128143301193" style="zoom:67%;" />

###### 构建聚合分析

```java
//3.1品牌聚合
TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
brand_agg.field("brandId").size(50);
    //3.1.1品牌聚合的子聚合，查询每个品牌的名称和图片
brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName"));
brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg"));
    //3.1.2拼接品牌聚合
sourceBuilder.aggregation(brand_agg);
//3.2分类聚合
TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
catalog_agg.field("catalogId").size(20);
    //3.2.1分类聚合的子聚合，查询分类名
catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName"));
    //3.2.2拼接分类聚合
sourceBuilder.aggregation(catalog_agg);
//3.3属性聚合
    //3.3.1指定嵌入聚合的名称和嵌入字段
NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
attr_agg.subAggregation(attr_id_agg);//指定嵌套查询的子聚合，查询属性id分布
    //3.3.2属性聚合的子聚合，查询属性名称和属性值
attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName"));
attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
    //3.3.3拼接嵌套聚合
sourceBuilder.aggregation(attr_agg);
```

###### 测试二

- 报错，而且创建的字符串中attrs属性名和值的聚合没有指定字段

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231128150915923.png" alt="image-20231128150915923" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231128151000768.png" alt="image-20231128151000768" style="zoom:67%;" />

  - 原因：我把指定字段写在了构建子聚合的外边了，相等于还是给父聚合指定字段

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231128151243916.png" alt="image-20231128151243916" style="zoom:67%;" />.

    ```java
    attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg")).
        field("attrs.attrName");
    attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg")).
        field("attrs.value").size(50);
    ```

  - 解决

    ```java
    attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName"));
    attr_id_agg.subAggregation(
           AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
    ```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231128150126550.png" alt="image-20231128150126550" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231128150109519.png" alt="image-20231128150109519" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231128151625678.png" alt="image-20231128151625678" style="zoom:67%;" />

###### 检索请求全代码

```java
private SearchRequest bulidSearchRequest(SearchParam param) {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//用于构建dsl语句
    //1.构建查询功能
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();//先构建bool query,用于组合多个查询条件
        //1.1.must-构建标题的模糊匹配
    if(!StringUtils.isEmpty(param.getKeyword())){
        boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
    }
    //1.2filter-不参与评分
        //1.2.1按照三级分类id查询
    if(param.getCatalog3Id()!=null){
        boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
    }
        //1.2.2按照多个品牌id查询
    if(param.getBrandId()!=null&&param.getBrandId().size()>0){
        boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
    }
        //1.2.3按照属性id进行查询【需要嵌入式处理】
    if(param.getAttrs()!=null&&param.getAttrs().size()>0){
        for (String attrStr : param.getAttrs()) {
            //同一种类型的属性的多个不同值用‘:’分割，如attr=1.5寸:8寸,属性id和属性值用‘_’分割
            String[] split = attrStr.split("_");//先将属性id和属性值分隔开
            String attrId=split[0];
            String[] attrValues = split[1].split(":");//同一种属性可能有多个值
            BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
            nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
            nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
            //每一个属性都必须单独指定嵌入式查询，不同属性之间
            NestedQueryBuilder nestedQuery = QueryBuilders.
                    nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);//ScoreMode指定按照什么参与评分
            boolQuery.filter(nestedQuery);
        }
    }
         //1.2.4按照库存进行查询 0无库存 1有库存
if(param.getHasStock()!=null){
    boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
}
        //1.2.5查询指定价格区间
    if(!StringUtils.isEmpty(param.getSkuPrice())){
        //【`1_500`表示1-500、`_500`表示500以下、`500_`表示500以上】
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
        String[] split = param.getSkuPrice().split("_");//按照‘_’进行分割
        if(split.length==2){
            //如果有两个值就说明指定了最大值和最小值
            rangeQuery.gte(split[0]).lte(split[1]);
        }else {
            if(param.getSkuPrice().startsWith("_")){
                rangeQuery.lte(split[0]);//只指定了上界
            }else {
                rangeQuery.gte(split[0]);//只指定了下界
            }
        }
        boolQuery.filter(rangeQuery);
    }
    //封装所有的查询条件
    sourceBuilder.query(boolQuery);
    //2.构建排序、分页、高亮
        //2.1排序,排序字段命名规则：排序字段名称_asc/desc
    if(!StringUtils.isEmpty(param.getSort())){
        String sort = param.getSort();
        String[] split = sort.split("_");
        SortOrder order=split[1].equalsIgnoreCase("asc")? SortOrder.ASC:SortOrder.DESC;
        sourceBuilder.sort(split[0],order);
    }
        //2.2分页
    sourceBuilder.from((param.getPageNum()-1)*EsConstant.PRODUCT_PAGESIZE);
    sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
        //2.3高亮
    if(!StringUtils.isEmpty(param.getKeyword())){
        //只有进行模糊匹配才需要标识高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("skuTitle");//需要高亮的字段
        highlightBuilder.preTags("<b style='color:red>");
        highlightBuilder.postTags("</b>");
        sourceBuilder.highlighter(highlightBuilder);
    }
    //3.聚合分析
        //3.1品牌聚合
    TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
    brand_agg.field("brandId").size(50);
            //3.1.1品牌聚合的子聚合，查询每个品牌的名称和图片
    brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName"));
    brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg"));
            //3.1.2拼接品牌聚合
    sourceBuilder.aggregation(brand_agg);
        //3.2分类聚合
    TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
    catalog_agg.field("catalogId").size(20);
            //3.2.1分类聚合的子聚合，查询分类名
    catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName"));
            //3.2.2拼接分类聚合
    sourceBuilder.aggregation(catalog_agg);
        //3.3属性聚合
            //3.3.1指定嵌入聚合的名称和嵌入字段
    NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
    TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
    attr_agg.subAggregation(attr_id_agg);//指定嵌套查询的子聚合，查询属性id分布
            //3.3.2属性聚合的子聚合，查询属性名称和属性值
    attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName"));
    attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
            //3.3.3拼接嵌套聚合
    sourceBuilder.aggregation(attr_agg);
    //4.构建请求并发送
    SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    return searchRequest;
}
```

##### 抽取返回结果数据

###### 代码

```java
private SearchResult bulidSearchResult(SearchResponse response,SearchParam param) {
    SearchResult result = new SearchResult();
    //1.返回所有查询到的商品
    SearchHits hits = response.getHits();
    List<SkuEsModel> esModelList=new ArrayList<>();
    for (SearchHit hit : hits.getHits()) {
        //遍历每一条记录
        String sourceAsString = hit.getSourceAsString();//获取真正的数据并返回json字符串
        SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);//转成json对象
        esModelList.add(esModel);
    }
    result.setProducts(esModelList);
    //2.当前所有商品涉及到的所有分类信息
    List<SearchResult.CatalogVo> catalogVos=new ArrayList<>();
    ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
    List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
    for (Terms.Bucket bucket : buckets) {
        SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
        //获取分类id
        String catalogId = bucket.getKeyAsString();
        catalogVo.setCatalogId(Long.valueOf(catalogId));
        //通过子聚合获取分类名
        ParsedStringTerms catalogNameAgg=bucket.getAggregations().get("catalog_name_agg");
        String catalogName=catalogNameAgg.getBuckets().get(0).getKeyAsString();//因为每个分类只对应一个分类名
        catalogVo.setCatalogName(catalogName);
        catalogVos.add(catalogVo);
    }
    result.setCatalogs(catalogVos);
    //3.当前所有商品涉及到的所有品牌信息
    List<SearchResult.BrandVo> brandVos=new ArrayList<>();
    ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
    for (Terms.Bucket bucket : brand_agg.getBuckets()) {
        SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
        //1.获取品牌id
        long brandId = bucket.getKeyAsNumber().longValue();
        brandVo.setBrandId(brandId);
        //2.通过id的子聚合获取品牌名字
        String brandName = (
                (ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")
        ).getBuckets().get(0).getKeyAsString();
        brandVo.setBrandName(brandName);
        //3.通过id的子聚合获取品牌图片
        String brandImg = (
                (ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")//先获取图片分布
        ).getBuckets().get(0).getKeyAsString();//一个品牌只对应一张图片
        brandVo.setBrandImg(brandImg);
        brandVos.add(brandVo);
    }
    result.setBrands(brandVos);
    //4.获取所有属性信息
    List<SearchResult.AttrVo> attrVos=new ArrayList<>();
    ParsedNested attr_agg = response.getAggregations().get("attr_agg");
    ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
    for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
        SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
        //1.获取属性id
        long attrId = bucket.getKeyAsNumber().longValue();
        attrVo.setAttrId(attrId);
        //2.通过id的子聚合获取属性名
        String attrName = (
                (ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")
        ).getBuckets().get(0).getKeyAsString();
        attrVo.setAttrName(attrName);
        //3.通过id的子聚合获取属性所有值
        List<String> attrValues = (
                (ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")
        ).getBuckets().stream().map(item->{
            return item.getKeyAsString();//获取每个属性值
        }).collect(Collectors.toList());
        attrVo.setAttrValue(attrValues);
        attrVos.add(attrVo);
    }
    result.setAttrs(attrVos);
    //========以上可以从聚合信息中获取到
    //5.分页信息
    result.setPageNum(param.getPageNum());//当前页码
    long total=hits.getTotalHits().value;//总记录数
    result.setTotal(total);
    //总页码计算 11/2=5...1,有余数就得多加一页
    int totalPages=(int)total%EsConstant.PRODUCT_PAGESIZE==0?
            (int)total/EsConstant.PRODUCT_PAGESIZE:
            (int)total/EsConstant.PRODUCT_PAGESIZE+1;//多一页来存放多余的不到一页的数据
    result.setTotalPages(totalPages);
    return result;
}
```

###### 测试

- 基本无误，但是没有封装高亮后的标题

  - 先判断是否指定了检索关键字，有的话就获取高亮后的skuTitle

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231128191139537.png" alt="image-20231128191139537" style="zoom:67%;" />.

```java
for (SearchHit hit : hits.getHits()) {
    //遍历每一条记录
    String sourceAsString = hit.getSourceAsString();//获取真正的数据并返回json字符串
    SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);//转成json对象
    if(!StringUtils.isEmpty(param.getKeyword())){
        HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
        String highlightTitle = skuTitle.getFragments()[0].string();
        esModel.setSkuTitle(highlightTitle);
    }
    esModelList.add(esModel);
}
```

#### 展示页面结果【了解，很乱，且冗余，而且超多bug】

##### 排序后的商品数据

- 同一个spu的不同sku为一组

- 没有高亮效果是因为th:text把文本中的标签转译成普通字符串了，使用**th:utext**就不会进行转义.

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231130131750378.png" alt="image-20231130131750378" style="zoom:67%;" />.

  - 使用th:utext之后没有显示文本，原因是拼接前缀标签的时候漏了一个`'`

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231130132631830.png" alt="image-20231130132631830" style="zoom:67%;" />![image-20231130132800089](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231130132800089.png)

    - ```java
      highlightBuilder.preTags("<b style='color:red>");
      改为
      highlightBuilder.preTags("<b style='color:red'>");
      ```

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231130133210376.png" alt="image-20231130133210376" style="zoom:67%;" />.

```html
<div class="rig_tab">
    <div th:each="product:${result.getProducts()}">
        <div class="ico">
            <i class="iconfont icon-weiguanzhu"></i>
            <a href="#">关注</a>
        </div>
        <p class="da">
            <a href="#" title="">
                <img th:src="${product.skuImg}" class="dim">
            </a>
        </p>
        <ul class="tab_im">
            <li><a href="#" title="黑色">
                <img th:src="${product.skuImg}"></a></li>
        </ul>
        <p class="tab_R">
            <span th:text="'￥'+${product.skuPrice}">¥5199.00</span>
        </p>
        <p class="tab_JE">
            <a href="#" th:utext="${product.skuTitle}">
            </a>
          </p>
        <p class="tab_PI">已有<span>11万+</span>热门评价
            <a href="#">二手有售</a>
        </p>
        <p class="tab_CP"><a href="#" title="谷粒商城Apple产品专营店">谷粒商城Apple产品...</a>
            <a href='#' title="联系供应商进行咨询">
                <img src="/static/search/img/xcxc.png">
            </a>
        </p>
        <div class="tab_FO">
            <div class="FO_one">
                <p>自营
                    <span>谷粒商城自营,品质保证</span>
                </p>
                <p>满赠
                    <span>该商品参加满赠活动</span>
                </p>
            </div>
        </div>
    </div>
</div>
```

##### 商品筛选栏

```html
<div class="JD_nav_logo">
    <!--品牌-->
    <div class="JD_nav_wrap">
        <div class="sl_key">
            <span>品牌：</span>
        </div>
        <div class="sl_value">
            <div class="sl_value_logo">
                <ul>
                    <li th:each="brand:${result.brands}">
                        <a href="#">
                            <img th:src="${brand.brandImg}" alt="">
                            <div th:text="${brand.brandName}">
                            </div>
                        </a>
                    </li>

                </ul>
            </div>
        </div>
        <div class="sl_ext">
            <a href="#">
                更多
                <i style='background: url("/static/search/image/search.ele.png")no-repeat 3px 7px'></i>
                <b style='background: url("/static/search/image/search.ele.png")no-repeat 3px -44px'></b>
            </a>
            <a href="#">
                多选
                <i>+</i>
                <span>+</span>
            </a>
        </div>
    </div>
    <!--分类-->
    <div class="JD_pre">
        <div class="sl_key">
            <span>分类：</span>
        </div>
        <div class="sl_value">
                <ul>
                    <li th:each="catalog:${result.catalogs}">
                        <a href="#" th:text="${catalog.catalogName}">5.56英寸及以上</a>
                    </li>
                </ul>
        </div>
        <div class="sl_ext">
            <a href="#">
                更多
                <i style='background: url("/static/search/image/search.ele.png")no-repeat 3px 7px'></i>
                <b style='background: url("/static/search/image/search.ele.png")no-repeat 3px -44px'></b>
            </a>
            <a href="#">
                多选
                <i>+</i>
                <span>+</span>
            </a>
        </div>
    </div>
    <!--遍历其它需要展示的属性-->
    <div class="JD_pre" th:each="attr:${result.attrs}">
        <div class="sl_key">
            <span th:text="${attr.attrName}">屏幕尺寸：</span>
        </div>
        <div class="sl_value">
            <ul>
                <li th:each="val:${attr.attrValue}"><a href="#" th:text="${val}">5.56英寸及以上</a></li>
            </ul>
        </div>
    </div>
</div>
```

##### 解决只查询到部分数据的问题

1. 原先**默认查询有库存**的商品，现在去掉这个默认条件，无论是否有库存都查询

   ```java
   private Integer hasStock=1;//是否只显示有货的数据,默认有库存
   改为
   private Integer hasStock;
   ```

2. 如果没有指定查询有库存的条件，那就查询所有商品

   ```java
   if(param.getHasStock()!=null){
       boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
   }
   ```

3. 测试<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231130152740995.png" alt="image-20231130152740995" style="zoom:67%;" />

##### 页面筛选条件渲染

1. 在当前路径基础上拼接条件

   - 如果只有一个查询条件，那需要以`?`开始

   ```java
   function searchProducts(name,value) {
       //在当前的连接继续拼接条件
       var href=location.href+"";
       if(href.indexOf("?")!=-1){
           location.href=href+"&"+name+"="+value;
       }else {
           location.href=href+"?"+name+"="+value;
       }
   }
   ```

2. 指定品牌条件【其它的也类似，不一一列举了】

   ```java
   <a href="#" th:href="${'javascript:searchProducts(&quot;brandId&quot;,'+brand.brandId}+')'">
   ```

3. 测试

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231130161358970.png" alt="image-20231130161358970" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231130161412158.png" alt="image-20231130161412158" style="zoom: 50%;" />

###### 导航搜索功能

1. 给检索关键字指定id

   -  th:value="${param.keyword}用于回显检索的关键词

   ```html
   <input id="keyword_input" type="text" placeholder="手机"  th:value="${param.keyword}"/>
   <a href="javascript:searchByKeyword();">搜索</a>
   ```

2. 获取到检索关键字，进行查询

   ```js
   function searchByKeyword(){
       searchProducts("keyword",$("#keyword_input").val());
   }
   ```

##### 其它功能【分页和面包屑导航没写】

###### 抽取路径重写方法

- 前面的路径重写也要用以下这个方法

```js
function replaceParamVal(url, paramName, replaceVal,forceAdd) {
    var oUrl = url.toString();
    var nUrl;
    if (oUrl.indexOf(paramName) != -1) {
        if( forceAdd ) {
            if (oUrl.indexOf("?") != -1) {
                nUrl = oUrl + "&" + paramName + "=" + replaceVal;
            } else {
                nUrl = oUrl + "?" + paramName + "=" + replaceVal;
            }
        } else {
            var re = eval('/(' + paramName + '=)([^&]*)/gi');
            nUrl = oUrl.replace(re, paramName + '=' + replaceVal);
        }
    } else {
        if (oUrl.indexOf("?") != -1) {
            nUrl = oUrl + "&" + paramName + "=" + replaceVal;
        } else {
            nUrl = oUrl + "?" + paramName + "=" + replaceVal;
        }
    }
    return nUrl;
};
```

###### 排序功能【非常冗余，直接复制源码】

1. 绑定排序事件，并且指定排序规则

   ```html
   <a class="sort_a"  sort="hotScore" href="#">综合排序</a>
   <a class="sort_a" sort="saleCount" href="#">销量</a>
   <a class="sort_a" sort="skuPrice" href="#">价格</a>
   ```

2. 排序事件

   ```js
   $(".sort_a").click(function () {
       changeStyle(this);
       let sort = $(this).attr("sort");//获取排序规则
       sort = $(this).hasClass("desc") ? sort + "_desc" : sort + "_asc";
       location.href = replaceParamVal(location.href, "sort", sort, false);
       return false;
     });
     function changeStyle(ele) {
         //先清空所有的样式
       $(".sort_a").css({"color": "#333", "border-color": "#ccc", "background": "#fff"});
       $(".sort_a").each(function () {
         let text = $(this).text().replace("↓", "").replace("↑", "");
         $(this).text(text);
       })
         //渲染正在点击的排序规则
       $(ele).css({"color": "#FFF", "border-color": "#e4393c", "background": "#e4393c"});
       $(ele).toggleClass("desc");
       if ($(ele).hasClass("desc")) {
         let text = $(ele).text().replace("↓", "").replace("↑", "");
         text = text + "↓";
         $(ele).text(text);
       } else {
         let text = $(ele).text().replace("↓", "").replace("↑", "");
         text = text + "↑";
         $(ele).text(text);
       }
     };
   ```

3. 回显样式

   - strings的相关方法中的参数无法取域中的数据，所以需要使用th:with先取出数据，然后指定给一个变量

   ```html
   <div class="filter_top_left" th:with="p = ${param.sort}, priceRange = ${param.skuPrice}">
       <a sort="hotScore"
          th:class="${(!#strings.isEmpty(p) && #strings.startsWith(p,'hotScore') && #strings.endsWith(p,'desc')) ? 'sort_a desc' : 'sort_a'}"
          th:attr="style=${(#strings.isEmpty(p) || #strings.startsWith(p,'hotScore')) ?
              'color: #fff; border-color: #e4393c; background: #e4393c;':'color: #333; border-color: #ccc; background: #fff;' }">
           综合排序[[${(!#strings.isEmpty(p) && #strings.startsWith(p,'hotScore') &&
           #strings.endsWith(p,'desc')) ?'↑':'↓' }]]</a>
       <a sort="saleCount"
          th:class="${(!#strings.isEmpty(p) && #strings.startsWith(p,'saleCount') && #strings.endsWith(p,'desc')) ? 'sort_a desc' : 'sort_a'}"
          th:attr="style=${(!#strings.isEmpty(p) && #strings.startsWith(p,'saleCount')) ?
              'color: #fff; border-color: #e4393c; background: #e4393c;':'color: #333; border-color: #ccc; background: #fff;' }">
           销量[[${(!#strings.isEmpty(p) && #strings.startsWith(p,'saleCount') &&
           #strings.endsWith(p,'desc'))?'↑':'↓' }]]</a>
       <a sort="skuPrice"
          th:class="${(!#strings.isEmpty(p) && #strings.startsWith(p,'skuPrice') && #strings.endsWith(p,'desc')) ? 'sort_a desc' : 'sort_a'}"
          th:attr="style=${(!#strings.isEmpty(p) && #strings.startsWith(p,'skuPrice')) ?
              'color: #fff; border-color: #e4393c; background: #e4393c;':'color: #333; border-color: #ccc; background: #fff;' }">
           价格[[${(!#strings.isEmpty(p) && #strings.startsWith(p,'skuPrice') &&
           #strings.endsWith(p,'desc'))?'↑':'↓' }]]</a>
       <a href="#">评论分</a>
       <a href="#">上架时间</a>
   </div>
   ```

###### 价格区间

1. 价格区间输入框

   ```js
   <input id="skuPriceFrom" type="number"
          th:value="${#strings.isEmpty(priceRange)?'':#strings.substringBefore(priceRange,'_')}"
          style="width: 100px; margin-left: 30px">
   -
   <input id="skuPriceTo" type="number"
          th:value="${#strings.isEmpty(priceRange)?'':#strings.substringAfter(priceRange,'_')}"
          style="width: 100px">
   <button id="skuPriceSearchBtn">确定</button>
   ```

2. 绑定按钮事件

   ```js
   $("#skuPriceSearchBtn").click(function () {
       let from = $(`#skuPriceFrom`).val();
       let to = $(`#skuPriceTo`).val();
       let query = from + "_" + to;
       location.href = replaceParamVal(location.href, "skuPrice", query,false);
   });
   ```

###### 查询有货商品

```html
<a th:with="check = ${param.hasStock}">
    <input id="showHasStock" type="checkbox" th:checked="${#strings.equals(check,'1')?true:false}">
    仅显示有货
</a>

$("#showHasStock").change(function () {
    alert( $(this).prop("checked") );
    if( $(this).prop("checked") ) {
        location.href = replaceParamVal(location.href,"hasStock",1,false);
    } else {
        let re = eval('/(hasStock=)([^&]*)/gi');
        location.href = (location.href+"").replace(re,"");
    }
    return false;
});
```

### 商品详情

#### 环境搭建

1. 主机域名部署，规定item.gulimall.com跳转到详情页【商品服务】，网关配置如下

   ```yaml
   - id: gulimall_host_route
     uri:  lb://gulimall-product
     predicates:
       - Host=gulimall.com,item.gulimall.com
   ```

2. cv商品详情页前端资源

3. 跳转某个商品详情页的控制器方法

   ```java
   @GetMapping("/{skuId}.html")
   public String skuItem(Long skuId){
       System.out.println("准备查询"+skuId+"号商品");
       return "item";
   }
   ```

4. 测试

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202162955343.png" alt="image-20231202162955343" style="zoom: 50%;" />.

5. 修改检索服务的跳转

   ```html
   <a th:href="|http://item.gulimall.com/${product.skuId}.html|" title="">
       <img th:src="${product.skuImg}" class="dim">
   </a>
   ```

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202163912739.png" alt="image-20231202163912739" style="zoom:67%;" />.

#### 封装业务数据

```java
//1.获取sku基本信息
SkuInfoEntity info;
//2.获取sku图片信息
List<SkuImagesEntity> images;
//3.获取当前sku对应的spu的所有销售属性组合
List<itemSaleAttrsVo> saleAttrs;
//4.获取spu的介绍
SpuInfoDescEntity desc;
//5.获取spu的规格参数信息
List<SpuItemAttrGroupVo> groupAttrs;
@Data
public class itemSaleAttrsVo {
    private Long attrId;
    private String attrName;
    private List<String> attrValues;
}
@Data
public class SpuItemAttrGroupVo {
    private  String groupName;
    private List<SpuBaseAttrVo> attrs;
}
@Data
public class SpuBaseAttrVo{
    private String attrName;
    private String attrValue;
}
```

#### 业务方法

##### 骨架

```java
public SkuItemVo item(Long skuId) {
    SkuItemVo skuItemVo=new SkuItemVo();
    //1.获取sku基本信息
    SkuInfoEntity info= getById(skuId);
    skuItemVo.setInfo(info);
    //获取后续可能要用到的数据
    Long spuId = info.getSpuId();
    Long catalogId = info.getCatalogId();
    //2.获取sku图片信息
    List<SkuImagesEntity> images=imagesService.getImagesBySkuId(skuId);
    skuItemVo.setImages(images);
    //3.获取当前sku对应的spu的所有销售属性组合
    List<itemSaleAttrsVo> saleAttrs=saleAttrValueService.getSaleAttrsBySpuId(spuId);
    skuItemVo.setSaleAttrs(saleAttrs);
    //4.获取spu的介绍
    SpuInfoDescEntity descEntity = spuInfoDescService.getById(spuId);
    skuItemVo.setDesc(descEntity);
    //5.获取spu的规格参数信息【其中的分组信息可以通过sku基本信息中的分类id来确定】
    List<SpuItemAttrGroupVo> attrGroupVos=attrGroupService.getAttrGroupWithAttrsBySpuId(spuId,catalogId);
    skuItemVo.setGroupAttrs(attrGroupVos);
    return skuItemVo;
}
```

##### 获取sku图片信息

- imagesService中根据skuId查询图片

```java
public List<SkuImagesEntity> getImagesBySkuId(Long skuId) {
   return this.baseMapper.selectList(new QueryWrapper<SkuImagesEntity>().eq("sku_id",skuId));
}
```

##### 获取spu的规格参数信息

###### 业务逻辑

- 多表联查手机分类下的13号商品的所有的属性分组，并且查询各个分组的所有属性值

```sql
SELECT ag.attr_group_name,ag.attr_group_id,aar.attr_id,attr.attr_name,pav.attr_value
FROM pms_attr_group ag
LEFT JOIN pms_attr_attrgroup_relation aar ON ag.attr_group_id=aar.attr_group_id #分组表和分组关联表连接查询到分组下的所有属性id
LEFT JOIN pms_attr attr on attr.attr_id=aar.attr_id #根据属性id去属性表查询属性名
LEFT JOIN pms_product_attr_value pav on pav.attr_id=aar.attr_id #根据属性id去属性值表中查询属性值
WHERE ag.catelog_id=225 and pav.spu_id=13
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231203224910584.png" alt="image-20231203224910584" style="zoom:67%;" />.

- service方法

```java
public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
    //查询当前spu对应的所有属性的分组信息和每个分组下的所有属性值【通过四表联查】
    List<SpuItemAttrGroupVo> vos=this.baseMapper.getAttrGroupWithAttrsBySpuId(spuId,catalogId);
    return vos;
}
```

- sql映射

```xml
List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") 
    Long spuId, @Param("catalogId") Long catalogId);
<resultMap id="spuItemAttrGroupVo" type="com.liao.gulimal.gulimalProduct.vo.SkuItemVo.SpuItemAttrGroupVo">
    <result property="groupName" column="attr_group_name"/>
    <collection property="attrs" ofType="com.liao.gulimal.gulimalProduct.vo.SkuItemVo.SpuBaseAttrVo">
        <result column="attr_name" property="attrName"/>
        <result column="attr_value" property="attrValue"/>
    </collection>
</resultMap>
<select id="getAttrGroupWithAttrsBySpuId"
        resultMap="spuItemAttrGroupVo">
    SELECT ag.attr_group_name, attr.attr_name, pav.attr_value
    FROM pms_attr_group ag
             LEFT JOIN pms_attr_attrgroup_relation aar ON ag.attr_group_id = aar.attr_group_id
             LEFT JOIN pms_attr attr
                       on attr.attr_id = aar.attr_id
             LEFT JOIN pms_product_attr_value pav on pav.attr_id = aar.attr_id
    WHERE ag.catelog_id = #{catalogId}
      and pav.spu_id = #{spuId}
</select>
```

###### 测试

```java
@Test
public void testGetSpuGroupInfo(){
    List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(13l, 225L);
    System.out.println(attrGroupWithAttrsBySpuId);
}
```

- 报错，原因无法解析内部类

  ```java
  Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'attrAttrgroupRelationDao' defined in file Unsatisfied dependency expressed through bean property 'sqlSessionFactory'
  org.apache.ibatis.type.TypeException: Could not resolve type alias 'com.liao.gulimal.gulimalProduct.vo.SkuItemVo.SpuItemAttrGroupVo'.  Cause: java.lang.ClassNotFoundException: Cannot find class:com.liao.gulimal.gulimalProduct.vo.SkuItemVo.SpuItemAttrGroupVo
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireByType(AbstractAutowireCapableBeanFactory.java:1534)
  ```

- 解决：把SkuItemVo中的内部类提取出来

  ```java
  [SpuItemAttrGroupVo(groupName=主体, attrs=[SpuBaseAttrVo(attrName=入网型号, attrValue=A2217)]), SpuItemAttrGroupVo(groupName=基本信息, attrs=[SpuBaseAttrVo(attrName=机身长度（mm）, attrValue=158.3), SpuBaseAttrVo(attrName=机身材质工艺, attrValue=以官网信息为准)]), SpuItemAttrGroupVo(groupName=主芯片, attrs=[SpuBaseAttrVo(attrName=CPU品牌, attrValue=以官网信息为准), SpuBaseAttrVo(attrName=CPU型号, attrValue=A13仿生)])]
  ```

##### 获取spu的所有sku的销售属性组合

- **老师挖坑**
  - 修改attrValues的封装方式：private List<String> attrValues==>private String attrValues;
  - 每个基本属性对应的所有销售属性用字符串表示，不同的销售属性用`,`分隔

- sql，查询所有销售属性，获取相同销售属性的分布，并且获取每个分布下的所有属性值

```sql
#查询spu对应的所有销售属性
SELECT ssav.attr_id,ssav.attr_name,
GROUP_CONCAT(DISTINCT ssav.attr_value) #连接当前分组，获取某个分组下的所有值并且去重
FROM pms_sku_info info
LEFT JOIN pms_sku_sale_attr_value ssav ON ssav.sku_id=info.sku_id
WHERE info.spu_id=13
GROUP BY ssav.attr_id #按属性id分组【一个属性会有不同属性值，把同一个属性的归为一组】
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231203234056327.png" alt="image-20231203234056327" style="zoom:67%;" />.

- sql映射

```xml
<select id="getSaleAttrsBySpuId" resultType="com.liao.gulimal.gulimalProduct.vo.itemSaleAttrsVo">
    SELECT ssav.attr_id attrId,ssav.attr_name attrName,
           GROUP_CONCAT(DISTINCT ssav.attr_value) attrValues
    FROM pms_sku_info info
        LEFT JOIN pms_sku_sale_attr_value ssav ON ssav.sku_id=info.sku_id
    WHERE info.spu_id=#{spuId}
    GROUP BY ssav.attr_id
</select>
```

- 测试结果

```java
[itemSaleAttrsVo(attrId=9, attrName=颜色, attrValues=白色,紫色,红色,绿色,黄色,黑色), itemSaleAttrsVo(attrId=12, attrName=版本, attrValues=128GB ,256GB,64GB)]
```

##### 页面渲染【了解】

- 显示是否有货【todo】

  - `<span th:text="${item.hasStock?'有货':'无货'}">无货</span>`
  - 需要给封装数据添加一个hasStock属性，用于存储是否有货

- 遍历所有的图片`<li th:each="img : ${item.images}"><img th:src="${img.imgUrl}"/></li>`

- 由于销售属性是拼接成字符串的，所有需要用`,`分割

  ```html
  <dt>选择[[${attr.attrName}]]</dt>
  <dd th:each="val : ${#strings.listSplit(attr.attrValues,',')}">
      <a>
          [[${val}]]
      </a>
  </dd>
  ```

- 遍历规格属性

  ```html
  <div class="guiGe" th:each="group : ${item.groupAttrs}">
      <h3 th:text="${group.groupName}">主体</h3>
      <dl>
          <div th:each="attr : ${group.attrs}">
              <dt th:text="${attr.attrName}">品牌</dt>
              <dd th:text="${attr.attrValue}">华为(HUAWEI)</dd>
          </div>
      </dl>
  </div>
  ```

##### sku组合切换【拓展】

###### 需求分析

- 查询不同销售属性对应的sku的交集，通过交集确定一个sku
  - 以属性id/属性名+属性值来分组【不同的sku可能会有相同的销售属性，即属性名下的属性值可能相同】
  - 分完组后连接当前分组获取到组内所有sku

###### 核心sql

```sql
SELECT ssav.attr_id,ssav.attr_name,ssav.attr_value,
GROUP_CONCAT(DISTINCT ssav.sku_id) skuList #连接当前分组，获取某种销售属性值对应的所有sku
FROM pms_sku_info info
LEFT JOIN pms_sku_sale_attr_value ssav ON ssav.sku_id=info.sku_id
WHERE info.spu_id=13
GROUP BY ssav.attr_id,ssav.attr_value #属性id和属性值共同组成一组
```

- sql映射

```xml
<resultMap id="itemSaleAttrsVo" type="com.liao.gulimal.gulimalProduct.vo.itemSaleAttrsVo">
    <result column="attr_id" property="attrId"/>
    <result column="attr_name" property="attrName"/>
    <collection property="attrValues" ofType="com.liao.gulimal.gulimalProduct.vo.AttrValueWithSkuIdVo">
        <result column="attr_value" property="attrValue"/>
        <result column="skuList" property="skuIds"/>
    </collection>
</resultMap>
<select id="getSaleAttrsBySpuId" resultMap="itemSaleAttrsVo">
    SELECT ssav.attr_id,ssav.attr_name,ssav.attr_value,
           GROUP_CONCAT(DISTINCT ssav.sku_id) skuList
    FROM pms_sku_info info
        LEFT JOIN pms_sku_sale_attr_value ssav ON ssav.sku_id=info.sku_id
    WHERE info.spu_id=#{spuId}
    GROUP BY ssav.attr_id,ssav.attr_value
</select>
```

###### 修改销售属性的封装

```java
public class itemSaleAttrsVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
public class AttrValueWithSkuIdVo {
    private String attrValue;
    private String skuIds;//某个销售属性的所有skuId，用逗号分割
}
```

###### 前端【了解】

- 修改页面渲染

```html
<dd th:each="val : ${attr.attrValues}">
    <a th:attr=" class=${#lists.contains(#strings.listSplit(val.skuIds,','),item.info.skuId.toString())
       ? 'sku_attr_value checked': 'sku_attr_value'}, skus=${val.skuIds} "
    >
        [[${val.attrValue}]]
    </a>
</dd>
```

- 绑定单击事件

```js
$(".sku_attr_value").click(function () {
    // 1、点击的元素添加上自定义的属性
    let skus = new Array();
    let curr = $(this).attr("skus").split(",");//获取当前元素的sku组合
    //去掉同一行所有的checked
    $(this).parent().parent().find(".sku_attr_value").removeClass("checked");
    $(this).addClass("checked");
    changeCheckedStyle();
    //获取其它行被选中的属性
    $("a[class='sku_attr_value checked']").each(function () {
        skus.push($(this).attr("skus").split(","));
    });
    let filterEle = skus[0];//存储每次过滤的结果
    for (let i = 1; i < skus.length; i++) {
        filterEle = $(filterEle).filter(skus[i]);//获取两个sku数组的交集
    }
    //取最终过滤后的结果【由于filterEle是数组，最终交集应该只有一个元素，所以取filterEle[0]】
    location.href = "http://item.gulimall.com/" + filterEle[0] + ".html";
    return false;
});
$(function () {
    changeCheckedStyle();
});
function changeCheckedStyle() {
    $(".sku_attr_value").parent().css({"border": "solid 1px #ccc"});
    $("a[class='sku_attr_value checked']").parent().css({"border": "solid 1px red"});
};
$(".addToCart").click(function () {
    let skuId = $(this).attr("skuId");
    let num = $("#productNum").val();
    location.href = "http://cart.gulimall.com/addCartItem?skuId=" + skuId + "&num=" + num;
    return false;
});
```

#### 异步编排

##### 创建业务线程池

###### 自定义属性配置绑定

- 自定义配置项

```java
@ConfigurationProperties(prefix = "gulimall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
```

- 导入自定义属性配置项的提示依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231204133157100.png" alt="image-20231204133157100" style="zoom:67%;" />.

- 属性配置

```properties
gulimall.thread.core-size=20
gulimall.thread.max-size=200
gulimall.thread.keep-alive-time=200
```

###### 线程池配置类

- 注入ThreadPoolExecutor组件，接收自定义线程池属性配置类作为参数

```java
@Configuration
public class MyThreadConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties properties){
        return new ThreadPoolExecutor(properties.getCoreSize(),properties.getMaxSize(),
                                      properties.getKeepAliveTime(), 
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
    }
}
```

##### 业务加上异步编排

```java
public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
    SkuItemVo skuItemVo = new SkuItemVo();
    CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
        //获取sku基本信息
        SkuInfoEntity info = getById(skuId);
        skuItemVo.setInfo(info);
        return info;
    }, executor);
    //以下三次异步任务都是基于获取sku基本信息任务，并且三个任务是并列的
    CompletableFuture<Void> saleAttrsFuture = infoFuture.thenAcceptAsync((res) -> {
        //根据获取sku基本信息任务查询到的基本信息的spuId，来获取spu的所有销售属性组合
        List<itemSaleAttrsVo> saleAttrs = saleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
        skuItemVo.setSaleAttrs(saleAttrs);
    }, executor);
    CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
        //根据获取sku基本信息务查询到的基本信息的spuId，来获取spu的介绍信息
        SpuInfoDescEntity descEntity = spuInfoDescService.getById(res.getSpuId());
        skuItemVo.setDesc(descEntity);
    }, executor);
    CompletableFuture<Void> baseAttrsFuture = infoFuture.thenAcceptAsync((res) -> {
        //根据获取sku基本信息任务查询到的基本信息的spuId和catalogId,获取spu的规格参数信息【其中的分组信息可以通过sku基本信息中的分类id来确定】
        List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.
                getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
        skuItemVo.setGroupAttrs(attrGroupVos);
    }, executor);
    CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
        //获取sku图片信息和获取sku基本信息是并列执行的
        List<SkuImagesEntity> images = imagesService.getImagesBySkuId(skuId);
        skuItemVo.setImages(images);
    }, executor);
    //等待所有任务都完成【可以不用等待infoFuture完成，因为有三个任务是基于infoFuture的，它们其中之一完成说明infoFuture早完成了】
    CompletableFuture.allOf(imageFuture,descFuture,saleAttrsFuture,baseAttrsFuture).get();
    return skuItemVo;
}
```

### 认证服务【蛮有意思的】

#### 环境搭建

1. 创建认证服务

2. 导入登录页面和注册页面，并将对应的静态资源放到nginx

3. 域名配置，auth.gulimall.com绑定认证服务

4. 配置网关

   ```yaml
   - id: gulimall_auth_route
     uri: lb://gulimall-auth-server
     predicates:
       - Host=auth.gulimall.com
   ```

#### 优化页面

##### 页面基础跳转

- 返回首页`<a href="http://gulimall.com">`

- 商品服务跳转登录、注册

  ```html
   <a href="http://auth.gulimall.com/login.html">你好，请登录</a>
   <a href="http://auth.gulimall.com/reg.html" class="li_2">免费注册</a>
  ```

- 登录页跳转注册页`<a href="http://auth.gulimall.com/reg.html">立即注册</a>`

- 注册页跳回登录页

##### 验证码功能

- 显示验证码倒计时<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231204180109826.png" alt="image-20231204180109826" style="zoom:80%;" />

```js
$(function () {
   $("#sendCode").click(function () {
      if($(this).hasClass("disabled")) {
         //正在倒计时中，就将倒计时按钮禁用
      } else {
         //1、给指定手机号发送验证码,需要绑定填写手机号的文本框
         $.get("/sms/sendCode?phone=" + $("#phoneNum").val(),function (data) {
            if(data.code != 0) {
               alert(data.msg);
            }
         });
      	 //2、开启倒计时
         timeoutChangeStyle();
      }
   });
});
var num = 60;
function timeoutChangeStyle() {
   $("#sendCode").attr("class","disabled");
   if(num == 0) {
      $("#sendCode").text("发送验证码");
      num = 60;
      $("#sendCode").attr("class","");//回复到0秒，去掉按钮的禁用功能
   } else {
      var str = num + "s 后再次发送";
      $("#sendCode").text(str);
      setTimeout("timeoutChangeStyle()",1000);//定时器方法，每个1秒执行一次【递归】
   }
   num --;
}
```

##### 简化空方法跳转【貌似没卵用呀】

- 配置视图控制器，并且配置路径映射

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }
}
```

#### 发送验证码

##### 环境搭建

1. 去阿里云申请一个认证服务
2. 根据接口文档测试
3. post测试结果

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231205142012227.png" alt="image-20231205142012227" style="zoom:67%;" />.

4. 后台测试短信验证功能

   - 按照提示下载httpUtils
   - 下载别人的api模板

   ```java
   String host = "https://gyytz.market.alicloudapi.com";
   String path = "/sms/smsSend";
   String method = "POST";
   String appcode = "6ca80ab7736c4666be72d368dcd8879c";
   Map<String, String> headers = new HashMap<String, String>();
   //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
   headers.put("Authorization", "APPCODE " + appcode);
   Map<String, String> querys = new HashMap<String, String>();
   querys.put("mobile", "13265114494");
   querys.put("param", "**code**:12345,**minute**:5");
   //smsSignId（短信前缀）和templateId（短信模板），可登录国阳云控制台自助申请。参考文档：http://help.guoyangyun.com/Problem/Qm.html
   querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
   querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
   Map<String, String> bodys = new HashMap<String, String>();
   try {
       /**
        * 重要提示如下:
        * HttpUtils请从\r\n\t    \t* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java\r\n\t    \t* 下载
        *
        * 相应的依赖请参照
        * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
        */
       HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
       System.out.println(response.toString());
       //获取response的body
       //System.out.println(EntityUtils.toString(response.getEntity()));
   } catch (Exception e) {
       e.printStackTrace();
   }
   ```

   - 测试结果

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231205142948983.png" alt="image-20231205142948983" style="zoom:67%;" />.

##### 抽取可配置组件

1. 抽取出发送短信方法

   ```java
   @Component
   @ConfigurationProperties(prefix = "spring.alicloud.sms")
   @Data
   public class SmsComponent {
       private String host;
       private String path;
       private String smsSignId;//签名
       private String templateId;//短信模板
       private String appcode;
       public void sendSmsCode(String phone,String code,String minute){
           String method = "POST";
           Map<String, String> headers = new HashMap<String, String>();
           //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
           headers.put("Authorization", "APPCODE " + appcode);
           Map<String, String> querys = new HashMap<String, String>();
           querys.put("mobile", phone);
           querys.put("param", "**code**:"+code+",**minute**:"+minute);
   //smsSignId（短信前缀）和templateId（短信模板），可登录国阳云控制台自助申请。参考文档：http://help.guoyangyun.com/Problem/Qm.html
           querys.put("smsSignId", smsSignId);
           querys.put("templateId", templateId);
           Map<String, String> bodys = new HashMap<String, String>();
           try {
               HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
               System.out.println(response.toString());
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
   ```

2. 配置

   ```yaml
   alicloud:
     sms:
       host: https://gyytz.market.alicloudapi.com
       path: /sms/smsSend
       templateId: 908e94ccf08b4476ba6c876d13f084ad
       smsSignId: 2e65b1bb3d054466b82f0c9d125465e2
       appcode: 6ca80ab7736c4666be72d368dcd8879c
   ```

3. 测试

   ```java
   @Autowired
   SmsComponent smsComponent;
   @Test
   void testSendSms() {
       smsComponent.sendSmsCode("13265114494","377099","3");
   }
   ```

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231205145110767.png" alt="image-20231205145110767" style="zoom:67%;" />.

##### 业务方法

###### 第三方服务

- 第三方服务对外提供发送验证码的方法

```java
@RestController
@RequestMapping("/sms")
public class SmsSendController {
    @Autowired
    SmsComponent smsComponent;
    //提供给其他服务进行调用
    @GetMapping
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code,
                      @RequestParam("mintue") String mintue){
        smsComponent.sendSmsCode(phone, code, mintue);
        return R.ok();
    }
}
```

###### 认证服务

- 远程调用接口

```java
@FeignClient("gulimall-third-part")
public interface ThirdPartFeignService {
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code,
                      @RequestParam("mintue") String mintue);
}
```

- 控制器方法

```java
@Controller
public class LoginController {
    @Autowired
    ThirdPartFeignService thirdPartFeignService;
    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone){
        String code = UUID.randomUUID().toString().substring(0, 5);
        thirdPartFeignService.sendCode(phone,code,"5");
        return R.ok();
    }
}
```

##### 60s内不能连续发验证码

- 如果用户连续发验证码，那就截取key中的存入验证码的时间，两次请求验证码的时间小于60s就返回错误

```java
//接口防刷
String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
if(!StringUtils.isEmpty(redisCode)){
    //如果缓存中有用户的验证码，就需要判断用户是否在60s内连续发送验证码
    long time = Long.parseLong(redisCode.split("_")[1]);
    if(System.currentTimeMillis()-time<60*1000){
        //在60s内不能连续发两次请求
        return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(),BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
    }
}
```

##### 存储验证码

- 将验证码临时存入redis，key为手机号+_+当前系统时间，value为验证码

```java
String code = UUID.randomUUID().toString().substring(0, 5);
int mintue=5;//验证码过期时间
thirdPartFeignService.sendCode(phone,code,mintue+"");
code=code+"_"+System.currentTimeMillis();
//临时存储验证码，用于验证码的再次校验，存key==phone，value==code+存储时间
redisTemplate.opsForValue().
    set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,code,mintue, TimeUnit.MINUTES);
```

#### 注册

##### 封装并且校验数据

```java
@Data
public class UserRegistVo {
    @NotEmpty(message = "用户名必须提交")
    @Length(min=6,max =18,message = "用户名必须是6-18位字符")
    private String userName;
    @NotEmpty(message = "密码必须填写")
    @Length(min=6,max =18,message = "密码必须是6-18位字符")
    private String password;
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    private String phone;
    @NotEmpty(message = "验证码不能为空")
    private String code;
}
```

##### 返回错误信息

- 注意事项

  - 如果校验出错，需要重定向到注册页，如果转发可能存在重复提交表单
  - 重定向需要指定域名，否则会暴露当前服务的ip地址

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231205165757035.png" alt="image-20231205165757035" style="zoom:67%;" />.

  - 使用RedirectAttributes，重定向也可以携带数据，会将数据存储在session域中
    - addFlashAttribute存放的数据使用一次就会被删除

```java
@PostMapping("/regist")
public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes attributes){
    if (result.hasErrors()) {
        //校验出错重定向到注册页，如果是转发可能存在重复提交表单
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : result.getFieldErrors()) {
            if(errors.containsKey(fieldError.getField())){
                //如果存在错误信息就拼接之前的
                errors.put(fieldError.getField(),errors.get(fieldError.getField())+"+"+
                        fieldError.getDefaultMessage());
            }else {
                errors.put(fieldError.getField(),fieldError.getDefaultMessage());
            }
        }
        //错误消息只回显一次，只要取出数据就删除session中的数据
        attributes.addFlashAttribute("errors", errors);
        //需要重定向到域名地址，如果不指定域名会暴露域名和端口信息
        return "redirect:http://auth.gulimall.com/reg.html";
    }
    //注册成功回到登录页
    return "redirect:http://auth.gulimall.com/reg";
}
```

##### 注册逻辑

1. 先校验验证码

   - 校验时需要把redis存储的字符串截取出验证码

   ```java
   String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
   if (!StringUtils.isEmpty(redisCode) && redisCode.split("_")[0].equals(vo.getCode())){
       //验证码校验通过，需要删除缓存中的验证码【令牌机制：用完就直接删,旧令牌就无法使用了】
       redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
   } else {
       //验证码过期
       Map<String, String> errors = new HashMap<>();
       errors.put("code", "验证码错误");
       attributes.addFlashAttribute("errors", errors);
       return "redirect:http://auth.gulimall.com/reg.html";
   }
   ```

2. 校验通过就调用远程会员服务的注册方法

   - 远程调用接口方法

   ```java
   @FeignClient("gulimall-member")
   public interface MemberFeginService {
       @PostMapping("gulimalmember/member/regist")
       public R regist(@RequestBody UserRegistVo vo);
   }
   ```

   - 认证服务控制器方法

   ```java
   R r = memberFeginService.regist(vo);
   if (r.getCode()==0) {
       //注册成功回到登录页
       return "redirect:http://auth.gulimall.com/login.html";
   }else {
       Map<String, String> errors=new HashMap<>();
       errors.put("msg",r.get("msg").toString());
       attributes.addFlashAttribute("errors", errors);
       return "redirect:http://auth.gulimall.com/reg.html";
   }
   ```

3. 会员服务的注册方法

   - 需要设置会员的默认等级

   - 设置用户名和手机号之前，需要检查是否唯一

     - 如果不唯一就抛出异常，让上层感知<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231205182544053.png" alt="image-20231205182544053" style="zoom:67%;" />

     ```java
     public class UserNameExistException extends RuntimeException{
         public UserNameExistException(String message) {
             super("用户名已经存在");
         }
     }
     接口方法
     void checkPhoneUnique(String phone)throws PhoneException;
     void checkUserNameUnique(String username)throws UserNameExistException;
     方法实现【两个校验都类似】
     public void checkPhoneUnique(String phone) throws PhoneException {
         Integer phoneCount = this.baseMapper.
                 selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
         if(phoneCount>0){
             throw new PhoneException() ;
         }
     }
     ```

   - 密码需要加密存储【而且需要不可逆存储，即密文无法通过算法反推出明文，详情请看拓展的**MD5&MD5盐值加密**】

   ```java
   public void regist(MemberRegistVo vo) {
       MemberEntity memberEntity = new MemberEntity();
       memberEntity.setLevelId(1l);//设置默认会员等级
       //检查用户名和手机号的是否唯一,可以使用异常机制让上层感知到
       checkPhoneUnique(vo.getPhone());
       checkUserNameUnique(vo.getUserName());
       memberEntity.setMobile(vo.getPhone());
       memberEntity.setUsername(vo.getUserName());
       //密码需要加密存储
       BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
       String encode = passwordEncoder.encode(vo.getPassword());
       memberEntity.setPassword(encode);
       this.baseMapper.insert(memberEntity);
   }
   ```

4. 会员服务的控制器方法需要处理业务方法抛出的异常

   ```java    @PostMapping("/regist")
   @PostMapping("/regist")
   public R regist(@RequestBody MemberRegistVo vo) {
       try {
           memberService.regist(vo);
       }catch (PhoneException e){
           return R.error(BizCodeEnume.PHONE_EXIST_EXCEPTION.getCode(),BizCodeEnume.PHONE_EXIST_EXCEPTION.getMsg());
       }catch (UserNameExistException e){
           return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(),BizCodeEnume.USER_EXIST_EXCEPTION.getMsg());
       }
       return R.ok();
   }
   ```

5. 认证服务完整的注册代码

   ```java
   @PostMapping("/regist")
   public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes attributes) {
       //RedirectAttributes作用是重定向携带数据,将数据存储在session域中
       if (result.hasErrors()) {
           //校验出错重定向到注册页，如果是转发可能存在重复提交表单
           Map<String, String> errors = new HashMap<>();
           for (FieldError fieldError : result.getFieldErrors()) {
               if (errors.containsKey(fieldError.getField())) {
                   //如果存在错误信息就拼接之前的
                   errors.put(fieldError.getField(), errors.get(fieldError.getField()) + "+" +
                           fieldError.getDefaultMessage());
               } else {
                   errors.put(fieldError.getField(), fieldError.getDefaultMessage());
               }
           }
           //错误消息只回显一次，只要取出数据就删除session中的数据
           attributes.addFlashAttribute("errors", errors);
           //需要重定向到域名地址，如果不指定域名会暴露域名和端口信息
           return "redirect:http://auth.gulimall.com/reg.html";
       }
       //注册
       //1.校验验证码
       String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
       if (!StringUtils.isEmpty(redisCode) && redisCode.split("_")[0].equals(vo.getCode())) {
           //验证码校验通过，需要删除缓存中的验证码【令牌机制：用完就直接删,旧令牌就无法使用了】
           redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
           R r = memberFeginService.regist(vo);
           if (r.getCode()==0) {
               //注册成功回到登录页
               return "redirect:http://auth.gulimall.com/login.html";
           }else {
               Map<String, String> errors=new HashMap<>();
               errors.put("msg",r.get("msg").toString());
               attributes.addFlashAttribute("errors", errors);
               return "redirect:http://auth.gulimall.com/reg.html";
           }
       } else {
           //验证码过期
           Map<String, String> errors = new HashMap<>();
           errors.put("code", "验证码错误");
           attributes.addFlashAttribute("errors", errors);
           return "redirect:http://auth.gulimall.com/reg.html";
       }
   }
   ```

#### 登录【其中单点登录有流程但是没整合，算是一个坑】

##### 普通登录

1. 接收前端数据，然后发给远程的会员服务进行校验

   - 封装登录数据

   ```java
   @Data
   public class UserLoginVo {
       private String loginAccount;
       private String password;
   }
   ```

2. 会员服务的登录校验功能

   - 控制层

   ```java
   @PostMapping("/login")
   public R login(@RequestBody MemberLoginVo vo) {
       MemberEntity entity=memberService.login(vo);
       if(entity==null){
           return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(),
                   BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
       }
       return R.ok();
   }
   ```

   - 业务层

   ```java
   public MemberEntity login(MemberLoginVo vo) {
       //1.先去数据库查询是否有该用户
       MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().
               eq("username", vo.getLoginAccount()).or().eq("mobile", vo.getLoginAccount()));
       if(entity==null){
           //没有该用户
           return null;
       }else {
           //2.有该用户就校验密码
           String password = entity.getPassword();
           BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
           //3.密码匹配
           boolean matches = passwordEncoder.matches(vo.getPassword(), password);
           if(matches){
               return entity;
           }else {
               return null;
           }
       }
   }
   ```

3. 远程调用会员服务的登录功能

   - 远程服务接口方法

     ```java
     @PostMapping("gulimalmember/member//login")
     R login(@RequestBody UserLoginVo vo);
     ```

   - 认证服务的登录方法

     ```java
     @PostMapping("/login")
     public String login(UserLoginVo vo,RedirectAttributes redirectAttributes){
         R login = memberFeginService.login(vo);
         if(login.getCode()==0){
             return "redirect:http://gulimall.com";//成功进入首页
         }else {
             Map<String, String> errors = new HashMap<>();
             errors.put("msg",login.get("msg").toString());
             redirectAttributes.addFlashAttribute("errors",errors);
             return "redirect:http://auth.gulimall.com/login.html";//失败返回登录页
         }
     }
     ```

##### 社交登录

###### 步骤

1. 用户点击 QQ 按钮

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231207125844593.png" alt="image-20231207125844593" style="zoom:67%;" />.

2. 引导跳转到 QQ 授权页 

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231207125834216.png" alt="image-20231207125834216" style="zoom:67%;" />.

3. 用户主动点击授权，跳回之前网页

###### 使用微博社交登录

- 官方文档：[授权机制说明 - 微博API (weibo.com)](https://open.weibo.com/wiki/授权机制说明)

1. 进入微博的开发平台，点击微连接的网站接入

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231207131625341.png" alt="image-20231207131625341" style="zoom: 33%;" />.

2. 创建自己的应用【要身份认证完才可以创建】，记住自己的 app key 和 app secret

   ```java
   App Key：490647606
   App Secret：796892e9f467eed7211d30fba5bdb593
   ```

3. 配置回调地址

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208132749995.png" alt="image-20231208132749995" style="zoom:67%;" />.

4. 参照文档的流程

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208123236090.png" alt="image-20231208123236090" style="zoom:50%;" />.

   1. 引导需要授权的用户到授权页：https://api.weibo.com/oauth2/authorize?client_id=YOUR_CLIENT_ID&response_type=code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI

      - 其中client_id需要填写App Key，redirect_uri需要填写授权成功跳转的页面

      ```html
      <a href="https://api.weibo.com/oauth2/authorize
      ?client_id=490647606&response_type=code&
               redirect_uri=http://auth.gulimall.com/oauth2.0/weibo/success">
         <img style="width: 50px;height: 18px" src="/static/login/JD_img/weibo.png" />
      </a>
      ```

      <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208124256846.png" alt="image-20231208124256846" style="zoom: 50%;" />.

   2. 如果用户同意授权，页面跳转至 YOUR_REGISTERED_REDIRECT_URI/?code=CODE

      <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208124445109.png" alt="image-20231208124445109" style="zoom:67%;" />.

   3. 使用上面的code换取Access Token：https://api.weibo.com/oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=authorization_code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE

      - 其中client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET可以使用basic方式加入header中

      - 使用Code换取AccessToken，**Code只能用一次** ，**同一个用户的accessToken一段时间是不会变化的**，即使多次获取

      - 返回的json结果

        ```json
        {
            "access_token": "SlAV32hkKG",
            "remind_in": 3600,
            "expires_in": 3600
        }
        ```

      - post发送请求

        <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208125307191.png" alt="image-20231208125307191" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208125249227.png" alt="image-20231208125249227" style="zoom: 80%;" />

   4. 使用获得的Access Token调用API，详情参照接口管理的已有权限

      <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208125836634.png" alt="image-20231208125836634" style="zoom:50%;" />.

      - 测试

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208125725835.png" alt="image-20231208125725835" style="zoom: 67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208125742585.png" alt="image-20231208125742585" style="zoom: 57%;" />

###### 处理社交登录流程

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208172500273.png" alt="image-20231208172500273" style="zoom:67%;" />

1. 构建post请求

   ```java
   HashMap<String, String> map = new HashMap<>();
   //redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE
   map.put("client_id","490647606");
   map.put("client_secret","796892e9f467eed7211d30fba5bdb593");
   map.put("grant_type","authorization_code");
   map.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");
   map.put("code",code);
   HttpResponse response = HttpUtils.doPost("api.weibo.com", "/oauth2/access_token",
           "post", null, null, map);
   ```

2. 处理响应结果

   - 如果成功，需要获取响应数据并且转化成实体对象

     ```java
     @Data
     public class SocialUser {
         private String access_token;
         private long remind_in;
         private long expires_in;
         private String uid;
         private String isRealName;
     }
     String json = EntityUtils.toString(response.getEntity());
     SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
     ```

3. 判断当前社交登录的用户是否为本系统的新用户，需要远程调用会员服务【如果是新用户就得注册会员信息】

   - 社交用户和会员信息要关联起来，需要额外存储uid字段、访问令牌和访问令牌的过期时间

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208162538175.png" alt="image-20231208162538175" style="zoom:80%;" />

   ```java
   private String socialUid;
   private String accessToken;
   private Long expiresIn;
   ```

   - 本地服务的远程调用逻辑

   ```java
   //远程接口方法
   @PostMapping("gulimalmember/member/oauth2/login")
   public R oauthLogin(@RequestBody SocialUser socialUser) throws Exception;
   //调用远程接口
   R r = memberFeginService.oauthLogin(socialUser);
   if(r.getCode()==0){
       MemberRespVo data = r.getData(new TypeReference<MemberRespVo>() {
       });
       log.info("登录成功，用户信息：{}",data.toString());
       return "redirect:http://gulimall.com";
   }else {
       return "redirect:http://auth.gulimall.com/login.html";
   }
   ```

   - 会员服务

     - 控制器方法

     ```java
     @PostMapping("/oauth2/login")
     public R oauthLogin(@RequestBody SocialUser socialUser) throws Exception {
         MemberEntity entity=memberService.login(socialUser);
         if(entity==null){
             return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(),
                     BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
         }
         return R.ok().put("data",entity);
     }
     ```

     - 业务方法

     ```java
     public MemberEntity login(SocialUser socialUser) throws Exception {
         //具有登录和注册合并逻辑
         String uid = socialUser.getUid();//获取社交用户的唯一标识
         //判断当前社交用户是否已经注册过本系统
         MemberEntity memberEntity = this.baseMapper.
             selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
         if(memberEntity!=null){
             //该用户注册过，需要给它更新本次登录的临时令牌和过期时间
             MemberEntity update=new MemberEntity();
             update.setId(memberEntity.getId());
             update.setAccessToken(socialUser.getAccess_token());
             update.setExpiresIn(socialUser.getExpires_in());
             this.baseMapper.updateById(update);
             memberEntity.setAccessToken(socialUser.getAccess_token());
             memberEntity.setExpiresIn(socialUser.getExpires_in());
             return memberEntity;
         }else {
             //没有查到当前用户，就说明没有在本系统注册过，本次登录需要进行注册
             MemberEntity regist = new MemberEntity();
             try {
                 //查询当前社交用户的社交账号信息，查询基本信息不能影响到注册操作，所以要try
                 HashMap<String, String> query = new HashMap<>();
                 query.put("access_token",socialUser.getAccess_token());
                 query.put("uid",socialUser.getUid());
                 HttpResponse response = HttpUtils.doGet("https://api.weibo.com",
                         "/2/users/show.json", "get", new HashMap<>(), query);
                 if(response.getStatusLine().getStatusCode()==200){
                     //查询成功
                     String json = EntityUtils.toString(response.getEntity());
                     JSONObject jsonObject = JSON.parseObject(json);
                     String name = jsonObject.getString("name");
                     String gender = jsonObject.getString("gender");
                     regist.setNickname(name);
                     regist.setGender("m".equalsIgnoreCase(gender)?1:0);
                 }
             }catch (Exception e){
     
             }
             regist.setSocialUid(socialUser.getUid());
             regist.setAccessToken(socialUser.getAccess_token());
             regist.setExpiresIn(socialUser.getExpires_in());
             this.baseMapper.insert(regist);
             return regist;
         }
     }
     ```

4. 认证服务的社交登录完整代码

   ```java
   @GetMapping("/oauth2.0/weibo/success")
   public String weibo(@RequestParam("code")String code) throws Exception {
       //1.根据code换取accessToken
       HashMap<String, String> map = new HashMap<>();
       //redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE
       map.put("client_id","490647606");
       map.put("client_secret","796892e9f467eed7211d30fba5bdb593");
       map.put("grant_type","authorization_code");
       map.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");
       map.put("code",code);
       HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token",
               "post", new HashMap<>(), new HashMap<>(), map);
       //2.处理
       if(response.getStatusLine().getStatusCode()==200){
           //获取到了accessToken
           String json = EntityUtils.toString(response.getEntity());
           SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
           //如果当前登录的社交用户是新用户，那就需要给新用户注册会员信息账号
           R r = memberFeginService.oauthLogin(socialUser);
           if(r.getCode()==0){
               MemberRespVo data = r.getData(new TypeReference<MemberRespVo>() {
               });
               log.info("登录成功，用户信息：{}",data.toString());
               return "redirect:http://gulimall.com";
           }else {
               return "redirect:http://auth.gulimall.com/login.html";
           }
       }else {
           return "redirect:http://auth.gulimall.com/login.html";
       }
   }
   ```

##### 单点登录

###### 案例引入

- 参照xxl-sso项目

1. 服务器编排

   - ssoserver.com作为登录认证服务器
   - client1.com
   - client2.com

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231209183241317.png" alt="image-20231209183241317" style="zoom: 80%;" />.

2. 修改认证服务器配置

   ```properties
   ### web
   server.port=8080
   server.servlet.context-path=/xxl-sso-server
   ### resources
   spring.mvc.servlet.load-on-startup=0
   spring.mvc.static-path-pattern=/static/**
   spring.resources.static-locations=classpath:/static/
   ### freemarker
   spring.freemarker.templateLoaderPath=classpath:/templates/
   spring.freemarker.suffix=.ftl
   spring.freemarker.charset=UTF-8
   spring.freemarker.request-context-attribute=request
   spring.freemarker.settings.number_format=0.##########
   ### xxl-sso
   xxl.sso.redis.address=redis://192.168.32.100:6379
   xxl.sso.redis.expire.minute=1440
   ```

3. 项目打包，在pom文件所在目录打开cmd窗口，输入`mvn clean package -Dmaven.skip.test=true`

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231209183820983.png" alt="image-20231209183820983" style="zoom: 50%;" />.

4. 启动ssoserver的jar包`java -jar xxl-sso-server-1.1.1-SNAPSHOT.jar`

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231209184052951.png" alt="image-20231209184052951" style="zoom: 50%;" />.

5. 访问http://ssoserver.com:8080/xxl-sso-server/

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231209184249746.png" alt="image-20231209184249746" style="zoom:67%;" />.

6. 修改客户端的配置【需要配置单点服务器的信息】，然后重新打包项目，并且重新启动服务器

   ```properties
   ### web
   server.port=8081
   server.servlet.context-path=/xxl-sso-web-sample-springboot
   ### resources
   spring.mvc.servlet.load-on-startup=0
   spring.mvc.static-path-pattern=/static/**
   spring.resources.static-locations=classpath:/static/
   ### freemarker
   spring.freemarker.templateLoaderPath=classpath:/templates/
   spring.freemarker.suffix=.ftl
   spring.freemarker.charset=UTF-8
   spring.freemarker.request-context-attribute=request
   spring.freemarker.settings.number_format=0.##########
   ### xxl-sso
   xxl.sso.server=http://ssoserver.com:8080/xxl-sso-server
   xxl.sso.logout.path=/logout
   xxl-sso.excluded.paths=
   xxl.sso.redis.address=redis://192.168.32.100:6379
   ```

7. 启动两个客户端，指定不同的启动端口java -jar xxl-sso-web-sample-springboot-1.1.1-SNAPSHOT.jar --server.port=8082

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231209204138449.png" alt="image-20231209204138449" style="zoom:67%;" />.

8. 两个客户端都访问彼此的项目http://client1.com:8081/xxl-sso-web-sample-springboot，都跳到认证服务器的登录页

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231209204926822.png" alt="image-20231209204926822" style="zoom:67%;" />.

9. 一个服务登录成功，其它服务也共享登录状态【一处登录/退出，处处登录/退出】

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231209205205004.png" alt="image-20231209205205004" style="zoom: 50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231209205224416.png" alt="image-20231209205224416" style="zoom: 50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231209205319260.png" alt="image-20231209205319260" style="zoom:50%;" />

###### 核心

- 多个系统即使域名不一样，想办法给多个系统**同步同一个用户的票据**
- 中央认证服务器：ssoserver.com
- 其他系统，想要登录就去ssoserver.com登录，登录成功跳转回各自的系统
- 只要有一个登录，其他都不用登录
- 全系统统一一个sso-sessionid，所有系统可能域名都不相同

###### 模拟流程

1. 创建用于测试的单点登录服务器模块和客户端模块

2. 客户端访问受限资源需要跳转到登录服务器进行认证，同时需要携带当前的路径才可以跳回

   - 如果登录成功会携带一个令牌，客户端通过这个令牌去登录服务器中查询到用户信息，然后**存储在自身服务**的session来标识登录
   - 以后访问受限服务，就直接获取自身服务的session的登录信息

   ```java
   @GetMapping(value = "/employees")
   public String employees(Model model, HttpSession session, @RequestParam(value = "token", required = false) String token) {
       if (!StringUtils.isEmpty(token)) {
           RestTemplate restTemplate = new RestTemplate();
           ResponseEntity<String> forEntity = restTemplate.getForEntity("http://ssoserver.com:8080/userinfo?token=" + token, String.class);
           String body = forEntity.getBody();
           session.setAttribute("loginUser", body);
       }
       Object loginUser = session.getAttribute("loginUser");
       if (loginUser == null) {
           return "redirect:" + "http://ssoserver.com:8080/login.html" + "?redirect_url=http://localhost:8081/employees";
       } else {
           List<String> emps = new ArrayList<>();
           emps.add("张三");
           emps.add("李四");
           model.addAttribute("emps", emps);
           return "employees";
       }
   }
   ```

3. 登录服务器中

   - 登录成功
     - 先将数据临时存进redis，然后返回一个token来等待客户端获取用户信息
     - 还需要将登录标识以cookie的形式存入登录服务器，之后的服务跳转到登录服务器，只要cookie中有登录标识，就不用登录

   ```java
   @PostMapping(value = "/doLogin")
   public String doLogin(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("redirect_url") String url, HttpServletResponse response) {
       //登录成功跳转，跳回到登录页
       if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
           String uuid = UUID.randomUUID().toString().replace("_", "");
           redisTemplate.opsForValue().set(uuid, username);
           Cookie sso_token = new Cookie("sso_token", uuid);
           response.addCookie(sso_token);
           return "redirect:" + url + "?token=" + uuid;
       }
       return "login";
   }
   ```

   - 跳转到登录服务器，先判断是否有登录标识

   ```java
   @GetMapping("/login.html")
   public String loginPage(@RequestParam("redirect_url") String url, Model model, @CookieValue(value = "sso_token", required = false) String sso_token) {
       if (!StringUtils.isEmpty(sso_token)) {
           return "redirect:" + url + "?token=" + sso_token;
       }
       model.addAttribute("url", url);
       return "login";
   }
   ```

   - 客户端如果携带令牌来查询用户数据，就去redis中查询指定token的用户并返回

   ```java
   @ResponseBody
   @GetMapping("/userinfo")
   public String userinfo(@RequestParam(value = "token") String token) {
       String s = redisTemplate.opsForValue().get(token);
       return s;
   }
   ```

###### 完整流程

- 首个**登录成功**的客户端要**给登录服务器留下登录痕迹cookie**，之后不同系统进入登录服务器就不用重新登录了
- 登录服务器要将token信息重定向的时候，带到url地址上
- 其他系统要处理url地址上的关键token，只要有token，就需要**将token对应的用户保存到<a>自己系统的session中</a>**
  - token对应的用户信息可以去登录服务器中获取
  - 自己系统将用户保存在自己的会话中
- 以后
  - 登录过的系统就直接在本系统的session中获取用户信息
  - 没登录的系统先跳转登录服务器，由于存在登录痕迹，所以跳过登录步骤直接返回token，然后存储用户信息到session

![image-20231210184316725](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231210184316725.png)

​    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231210184302677.png" alt="image-20231210184302677" style="zoom: 73.5%;" />

#### 整合Spring Session

##### 环境搭建

1. 导入依赖

   ```xml
   <dependency>
       <groupId>org.springframework.session</groupId>
       <artifactId>spring-session-data-redis</artifactId>
   </dependency>
   ```

2. 配置Spring Session的保存类型`spring.session.store-type=redis`

3. 配置redis连接信息

4. 开启整合redis作为session存储功能`@EnableRedisHttpSession`

##### 修改业务代码

1. 将登录成功的用户数据放到session中`session.setAttribute("loginUser",data);`

2. 报错：无法将对象远程存储到redis

   - 解决：对象需要序列化成二进制流才可以远程传输，所以需要实现序列化接口

   ```properties
   There was an unexpected error (type=Internal Server Error, status=500).
   Cannot serialize; nested exception is org.springframework.core.serializer.support.SerializationFailedException: Failed to serialize object using DefaultSerializer; nested exception is java.lang.IllegalArgumentException: DefaultSerializer requires a Serializable payload but received an object of type [com.liao.gulimall.gulimallauthserver.vo.MemberRespVo]
   org.springframework.data.redis.serializer.SerializationException: Cannot serialize; nested exception is org.springframework.core.serializer.support.SerializationFailedException: Failed to serialize object using DefaultSerializer; nested exception is java.lang.IllegalArgumentException: DefaultSerializer requires a Serializable payload but received an object of type [com.liao.gulimall.gulimallauthserver.vo.MemberRespVo]
   ```

3. redis会存储session数据

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208195958483.png" alt="image-20231208195958483" style="zoom:67%;" />.

4. 但是无法解决子域名共享问题，先**<a>手动</a>修改sessionId的范围**，然后商品服务引入spring session，指定存储类型且开启存储功能

5. 报错

   - 浏览器报错：序列化异常，无法反序列化

   ```properties
   org.springframework.data.redis.serializer.SerializationException: Cannot deserialize; nested exception is org.springframework.core.serializer.support.SerializationFailedException: Failed to deserialize payload. Is the byte array a result of corresponding serialization for DefaultDeserializer?; nested exception is org.springframework.core.NestedIOException: Failed to deserialize object type; nested exception is java.lang.ClassNotFoundException: com.liao.gulimall.gulimallauthserver.vo.MemberRespVo
   org.springframework.data.redis.serializer.JdkSerializationRedisSerializer.deserialize(JdkSerializationRedisSerializer.java:84)
   org.springframework.data.redis.core.AbstractOperations.deserializeHashValue(AbstractOperations.java:380)
   org.springframework.data.redis.core.AbstractOperations.deserializeHashMap(AbstractOperations.java:324)
   	org.springframework.data.redis.core.DefaultHashOperations.entries(DefaultHashOperations.java:309)
   org.springframework.data.redis.core.DefaultBoundHashOperations.entries(DefaultBoundHashOperations.java:223)
   ```

   - 商品服务控制台报错：因为商品服务没有gulimallauthserver.vo.MemberRespVo，所以反序列化失败

   ```properties
   Caused by: java.lang.ClassNotFoundException: com.liao.gulimall.gulimallauthserver.vo.MemberRespVo
   	at java.net.URLClassLoader.findClass(URLClassLoader.java:382) ~[na:1.8.0_202]
   	at java.lang.ClassLoader.loadClass(ClassLoader.java:424) ~[na:1.8.0_202]
   	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:349) ~[na:1.8.0_202]
   	at java.lang.ClassLoader.loadClass(ClassLoader.java:357) ~[na:1.8.0_202]
   ```

6. 解决：将认证服务的MemberRespVo放在公共模块作为to<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208202700049.png" alt="image-20231208202700049" style="zoom:67%;" />

7. 普通登录成功也需要存入session

   ```java
   session.setAttribute(AuthServerConstant.LOGIN_USER,login.getData(new TypeReference<MemberRespVo>(){}))
   ```

8. 修改进入登录页的逻辑，需要判断session是否有用户信息，有的话直接跳转首页

   ```java
   @GetMapping("/lgoin.html")
   public String loginPage(HttpSession session){
       Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
       if(attribute==null){
           //用户还没登录
           return "login";
       }else {
           return "redirect:http://auth.gulimall.com/reg.html";
       }
   }
   ```

##### 改进

- 解决子域名共享问题和序列化问题【改用json序列化】，session配置类如下【涉及到的服务都需要有该配置】
  - 注意：指定父域名不需要加前缀`.`

```java
@Configuration
public class SessionConfig {
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setDomainName("gulimall.com");//明确指定是整个父域名
        cookieSerializer.setCookieName("GULISESSION");//修改sessionId的名称
        return cookieSerializer;
    }
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208204931194.png" alt="image-20231208204931194" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208205005436.png" alt="image-20231208205005436" style="zoom:80%;" />

#### 拓展

##### MD5&MD5盐值加密

- MD5：Message Digest algorithm 5，信息摘要算法
  - 压缩性：任意长度的数据，算出的MD5值长度都是固定的
  - 容易计算：从原数据计算出MD5值很容易
  - 抗修改性：对原数据进行任何改动，哪怕只修改1个字节，所得到的MD5值都有很大区别
    - 抗修改性会使密文被暴力破解，因为相同的数据md5值相同，所以md5不能直接用来加密
  - 强抗碰撞：想找到两个不同的数据，使它们具有相同的MD5值，是非常困难的
  - 不可逆
- 加盐：通过**生成随机数**与MD5生成字符串进行组合 
  - 数据库同时存储MD5值与salt值【盐值一样，获取到的随机数也是一样的】
  - 验证正确性时，将明文进行salt值的md5转义即可
  - spring家族的BCryptPasswordEncoder可以生成不同的密文，并且可以和明文匹配，就不需要存储盐值了

##### OAuth2.0

- OAuth： OAuth（开放授权）是一个开放标准，允许用户授权第三方网站访问他们存储在另外的服务提供者上的信息，而不需要将用户名和密码提供给第三方网站或分享他们数据的所有内容

- OAuth2.0：对于用户相关的 OpenAPI（例如获取用户信息，动态同步，照片，日志，分享等），为了保护用户数据的安全和隐私，第三方网站访问用户数据前都需要显式的向用户征求授权

- 官方版流程

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208123107957.png" alt="image-20231208123107957" style="zoom: 50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231207130849791.png" alt="image-20231207130849791" style="zoom: 45%;" />

  - 概念
    - Client指第三方应用
    - Resource Owner指用户
    - Authorization Server是我们的授权服务器
    - Resource Server是API服务器
  - 步骤
    - （A）用户打开客户端以后，客户端**要求用户给予授权**
    - （B）用户同意给予客户端授权
    - （C）客户端使用上一步获得的授权，向认证服务器申请令牌
    - （D）认证服务器对客户端进行认证以后，确认无误，同意发放令牌
    - （E）客户端使用令牌，向资源服务器申请获取资源
    - （F）资源服务器确认令牌无误，同意向客户端开放资源

### 购物车

#### 环境搭建

1. 创建购物车模块，配置域名映射cart.gulimall.com，然后在nginx创建cart的静态文件目录

2. 配置网关路由

   ```yaml
   - id: gulimall_cart_route
     uri: lb://gulimall-cart
     predicates:
       - Host=cart.gulimall.com
   ```

3. 后续用redis存储购物车数据，所以需要导入redis依赖且配置信息

#### 购物车需求

##### 需求描述

- 用户可以在**登录状态**下将商品添加到购物车【用户购物车/在线购物车】 
  - 放入数据库 - mongodb
  - 放入 redis（采用） ，登录以后，会将临时购物车的数据全部合并过来，并清空临时购物车
- 用户可以在**未登录状态**下将商品添加到购物车【游客购物车/离线购物车/临时购物车】 
  - 放入 localstorage（客户端存储，后台不存）
  - cookie - WebSQL
  - 放入redis（采用）， 浏览器即使关闭，下次进入，临时购物车数据都在，然后**cookie中需要存入临时用户id**
- 用户可以
  - 使用购物车一起结算下单
  - 给购物车添加商品
  - 查询自己的购物车
  - 在购物车中修改购买商品的数量
  - 在购物车中删除商品
  - 选中不选中商品【反选】 
  - 在购物车中展示商品优惠信息 - 提示购物车商品价格变化

##### 数据结构

- 购物项分析

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231211110614327.png" alt="image-20231211110614327" style="zoom: 50%;" />.

  - 每一个购物项信息，都是一个对象，基本字段包括

  ```json
  {
  	skuId: 2131241, check: true, title: "Apple iphone.....",
      defaultImage: "...", price: 4999, count: 1,totalPrice: 4999, skuSaleVO: {...}
  }
  ```

  - 另外，购物车中不止一条数据，因此最终会是对象的集合

- 数据结构设计

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231211111417305.png" alt="image-20231211111417305" style="zoom:50%;" />.

  - 首先**不同用户应该有独立的购物车**，因此购物车应该**以用户的id作为key** 来存储，**Value是用户购物车的<a>所有购物项</a>信息**。这样看来基本的`k-v`结构就可以了
  - 但是，对购物车中的商品进行增、删、改操作，基本都需要根据商品id进行判断， 为了方便后期处理，购物项也应该是`k-v`结构，key 是商品 id，value 才是这个商品的购物车信息
  - 综上所述，购物车结构是一个双层 Map：Map<String,Map<String,String>>
    - 第一层 Map，Key 是用户 id
    - 第二层 Map，Key 是购物车中商品 id，值是购物项数据
  - Map<String k1,Map<String k2,CartItemInfo>>
    - k1:标识每一个用户的购物车id
    - k2：购物项的商品id 
    - 在redis中，key:用户标识，value:Hash（k：商品id，v：购物项详情）

#### 封装vo

- 注意：需要计算的属性都要重写get方法，保证每次获取属性都会进行计算

- 购物车

```java
@Data
public class Cart {
    List<CartItem> items;
    private Integer countNum;//商品数量
    private Integer countType;//商品类型数量
    private BigDecimal totalAmount;//商品总价
    private BigDecimal reduce=new BigDecimal(0);//减免价格
    //计算商品总量
    public Integer getCountNum() {
        int sum=0;
        if(items!=null&&items.size()>0){
            for (CartItem item : items) {
                sum+=item.getCount();
            }
        }
        return sum;
    }
    //计算包含的商品类型数量
    public Integer getCountType() {
        int count=0;
        if(items!=null&&items.size()>0){
            for (CartItem item : items) {
                count++;
            }
        }
        return countType;
    }
    //计算总价
    public BigDecimal getTotalAmount() {
        BigDecimal amount=new BigDecimal(0);
        //先计算购物项的总价
        if(items!=null&&items.size()>0){
            for (CartItem item : items) {
                if(item.getCheck()){
                    //只计算被选中商品的总价
                    amount=amount.add(item.getTotalPrice());
                }
            }
        }
        //然后减去减免价格
        amount=amount.subtract(getReduce());
        return amount;
    }
}
```

- 购物项

```java
@Data
public class CartItem {
    private Long skuId;
    private Boolean check=true;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;//单价
    private Integer count;//总数
    private BigDecimal totalPrice;//总价
    public BigDecimal getTotalPrice() {
        //计算总价
        return this.price.multiply(new BigDecimal(this.count));
    }
}
```

#### 业务功能

##### 临时购物车分析

- 浏览器会保存一个临时用户id作为临时购物车的标识

- 如果没有临时用户就得先创建出一个，这一块可以用拦截器来拦截

- 一次请求使用的是同一个线程，所以可以使用ThreadLocal共享同一个线程的数据

- Map<Thread,Object> threadLocal

- 拦截器

  - 业务执行之前，封装登录的用户id和临时用户id，如果没有临时用户就必须创建一个
  - 业务执行之后，如果浏览器中没有临时用户信息，就需要把刚刚生成的临时用户存入cookie

  ```java
  @Component
  public class CartInterceptor implements HandlerInterceptor {
      public static ThreadLocal<UserInfoTo> threadLocal=new ThreadLocal();
      @Override
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
          UserInfoTo userInfoTo = new UserInfoTo();
          HttpSession session = request.getSession();
          MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
          if(memberRespVo!=null){
              //用户登陆了
              userInfoTo.setUserId(memberRespVo.getId());
          }
          Cookie[] cookies = request.getCookies();
          if(cookies!=null && cookies.length>0){
              for (Cookie cookie : cookies) {
                  String name=cookie.getName();
                  if(CartConstant.TEMP_USER_COOKIE_NAME.equals(name)){
                      userInfoTo.setUserKey(name);
                      break;
                  }
              }
          }
          if(userInfoTo.getUserKey()==null){
              //没有临时用户就必须创建一个
              userInfoTo.setUserKey( UUID.randomUUID().toString());
          }
          threadLocal.set(userInfoTo);//存储到threadLocal，供同一个请求共享
          return true;
      }
      @Override
      public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
          UserInfoTo userInfoTo = threadLocal.get();
          if(!userInfoTo.isLogin()){
              //如果用户未登录，就得把临时用户存入cookie
              Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
              cookie.setDomain("gulimall.com");//整个项目都携带
              cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
              response.addCookie(cookie);
          }
      }
  }
  ```

- WebConfig添加拦截器组件和拦截路径

  ```java
  @Configuration
  public class WebConfig implements WebMvcConfigurer {
      @Override
      public void addInterceptors(InterceptorRegistry registry) {
          registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
      }
  }
  ```

##### 添加商品到购物车

###### 控制器方法

- 后端需要收集加入购物车的商品id以及商品数量，添加完商品之后需要回显添加的购物项

```java
@GetMapping("addCartItem")
public String addCartItem(@RequestParam("skuId")Long skuId, @RequestParam("num")int num
, Model model) throws ExecutionException, InterruptedException {
    CartItem cartItem=cartService.addCart(skuId,num);
    model.addAttribute("cartItem",cartItem);
    return "success";
}
```

###### 获取用户信息并且封装购物车的redis操作对象

- 使用threadLocal获取到本次的用户信息，判断用户是否登录
  - 如果是在线用户就操作在线购物车，否则操作离线购物车
  - boundHashOps方法可以绑定一个键，之后都操作该键的数据，可以将声明**操作购物车数据的对象**抽取成一个方法

```java
private BoundHashOperations<String, Object, Object>  getCartOps() {
    UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
    String cartKey=CartConstant.CART_PREFIX;
    if(userInfoTo.getUserId()!=null){
        cartKey+=userInfoTo.getUserId();
    }else {
        cartKey+=userInfoTo.getUserKey();
    }
    return redisTemplate.boundHashOps(cartKey);
}
```

###### 远程查询

- 远程查询商品基本信息

```java
@FeignClient("gulimall-product")
public interface ProductFeignService {
    @RequestMapping("/gulimalProduct/skuinfo/info/{skuId}")
     R info(@PathVariable("skuId") Long skuId);
}
```

- 远程查询商品销售属性的字符串列表

```java
@RequestMapping("/gulimalProduct/skusaleattrvalue/stringList/{skuId}")
List<String> getStringListById(@PathVariable("skuId") Long skuId);
//远程服务的业务方法
@RequestMapping("/stringList/{skuId}")
public List<String> getStringListById(@PathVariable("skuId") Long skuId){
    return skuSaleAttrValueService.getStringListById(skuId);
}
<select id="getStringListById" resultType="java.lang.String">
    select concat(attr_name, ":", attr_value)
    from `pms_sku_sale_attr_value`
    where sku_id=#{skuId}
</select>
```

###### 业务方法

- 购物项包括销售属性信息和基本信息，可以使用多线程加速查询，所以需要导入线程池等配置类和配置项

```java
public CartItem addCart(Long skuId, int num) throws ExecutionException, InterruptedException {
    //1.获取购物车的操作对象
    BoundHashOperations<String, Object, Object> cartOps = getCartOps();
    CartItem cartItem = new CartItem();
    //2.远程查询要添加的商品基本信息
    CompletableFuture<Void> getBaseInfo = CompletableFuture.runAsync(() -> {
        R skuInfo = productFeignService.info(skuId);
        SkuInfoVo data = skuInfo.getData("skuInfo",new TypeReference<SkuInfoVo>() {
        });
        //2.1.同步购物项信息
        cartItem.setCheck(true);
        cartItem.setCount(num);
        cartItem.setImage(data.getSkuDefaultImg());
        cartItem.setTitle(data.getSkuTitle());
        cartItem.setSkuId(skuId);
        cartItem.setPrice(data.getPrice());
    }, executor);
    //3.远程查询sku的销售属性
    CompletableFuture<Void> getSaleAttrs = CompletableFuture.runAsync(() -> {
        List<String> saleAttrs = productFeignService.getStringListById(skuId);
        cartItem.setSkuAttr(saleAttrs);
    }, executor);
    //4.等待所有异步任务查询完成就存取数据
    CompletableFuture.allOf(getBaseInfo, getSaleAttrs).get();
    cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    return cartItem;
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231212232151529.png" alt="image-20231212232151529" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231212232428826.png" alt="image-20231212232428826" style="zoom:67%;" />

###### 业务改进

- 如果商品是第一次加入购物车就得重复上述逻辑
- 如果购物车有当前商品数据，那就直接给该商品数量+num即可

```java
public CartItem addCart(Long skuId, int num) throws ExecutionException, InterruptedException {
    //1.获取购物车的操作对象
    BoundHashOperations<String, Object, Object> cartOps = getCartOps();
    String res = (String) cartOps.get(skuId.toString());
    if(StringUtils.isEmpty(res)){
        //购物车无此商品
        CartItem cartItem = new CartItem();
        //2.远程查询要添加的商品基本信息
        CompletableFuture<Void> getBaseInfo = CompletableFuture.runAsync(() -> {
            R skuInfo = productFeignService.info(skuId);
            SkuInfoVo data = skuInfo.getData("skuInfo",new TypeReference<SkuInfoVo>() {
            });
            //2.1.同步购物项信息
            cartItem.setCheck(true);
            cartItem.setCount(num);
            cartItem.setImage(data.getSkuDefaultImg());
            cartItem.setTitle(data.getSkuTitle());
            cartItem.setSkuId(skuId);
            cartItem.setPrice(data.getPrice());
        }, executor);
        //3.远程查询sku的销售属性
        CompletableFuture<Void> getSaleAttrs = CompletableFuture.runAsync(() -> {
            List<String> saleAttrs = productFeignService.getStringListById(skuId);
            cartItem.setSkuAttr(saleAttrs);
        }, executor);
        //4.等待所有异步任务查询完成就存取数据
        CompletableFuture.allOf(getBaseInfo, getSaleAttrs).get();
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
        return cartItem;
    }else {
        //购物车有当前商品，就修改数量即可
        CartItem item = JSON.parseObject(res, CartItem.class);
        item.setCount(item.getCount()+num);
        cartOps.put(skuId.toString(),JSON.toJSONString(item));
        return item;
    }
}
```

- 为了防止刷新重复提交数据，所以需要进行重定向，然后重定向到指定控制器方法之后需要再次查询本次提交的信息
- RedirectAttributes ra
  - ra.addFlashAttribute();将数据放在session里面可以在页面取出，但是只能取一次
  - ra.addAttribute("skuId",skuId);将数据放在请求参数中

```java
@GetMapping("addCartItem")
public String addCartItem(@RequestParam("skuId")Long skuId, @RequestParam("num")int num
, RedirectAttributes attributes) throws ExecutionException, InterruptedException {
    cartService.addCart(skuId,num);
    attributes.addAttribute("skuId",skuId);//会携带在请求参数
    return "redirect:http://cart.gulimall.com/addCartItem.html";
}
@GetMapping("addCartItem.html")
public String addCartItemToSuccess(Model model,@RequestParam("skuId")String skuId) {
    CartItem cartItem=cartService.getCartItemBySkuId(skuId);//获取购物车中某个购物项的信息
    model.addAttribute("cartItem",cartItem);
    return "success";
}
//获取购物车中某个购物项的信息
public CartItem getCartItemBySkuId(String skuId) {
    BoundHashOperations<String, Object, Object> cartOps = getCartOps();
    String res= (String) cartOps.get(skuId);
    return JSON.parseObject(res,CartItem.class);
}
```

##### 获取购物车

###### 控制器方法

```java
@GetMapping("/cart.html")
public String cartListPage(Model model) throws ExecutionException, InterruptedException {
    //获取同一个线程【同一次请求】的共享数据
    Cart cart=cartService.getCart();
    model.addAttribute("cart",cart);
    return "cartList";
}
```

###### 业务方法

- 需要区分登录用户和临时用户

  - 登录后的用户需要将临时购物车里的数据合并【即临时购物车有数据就要合并】

  - 获取不同类型用户购物车的所有购物项可以抽取出一个方法

    ```java
    public List<CartItem> getCartItems(String key){
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        List<Object> values = operations.values();
        if(values!=null&&values.size()>0){
            return values.stream().map((item) -> {
                return JSON.parseObject((String) item, CartItem.class);
            }).collect(Collectors.toList());
        }
        return null;
    }
    ```

  - 合并完需要清空购物车`redisTemplate.delete(userKey);`

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213131459559.png" alt="image-20231213131459559" style="zoom: 50%;" />.

```java
public Cart getCart() throws ExecutionException, InterruptedException {
    UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
    Cart cart = new Cart();
    if (userInfoTo.getUserId() != null) {
        String userId = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        String userKey=CartConstant.CART_PREFIX + userInfoTo.getUserKey();
        //1.先检查临时购物车的数据是否已经合并
        List<CartItem> tempCartItems = getCartItems(userKey);
        //2.合并购物车
        if(tempCartItems!=null&&tempCartItems.size()>0){
            //2.1临时购物车还有数据则需要合并
            for (CartItem tempCartItem : tempCartItems) {
                //因为当前有用户登录，添加购物项的操作是添加给登录用户的
                addCart(tempCartItem.getSkuId(),tempCartItem.getCount());
            }
            //2.2合并完后需要删除临时购物车的键
            redisTemplate.delete(userKey);
        }
        //3.获取登陆后的购物车的数据【包含合并的临时购物车的数据】
        List<CartItem> cartItems = getCartItems(userId);
        if(cartItems!=null&&cartItems.size()>0){
            cart.setItems(cartItems);
        }
    } else {
        //没登陆就获取临时购物车的所有购物项
        List<CartItem> cartItems = getCartItems(CartConstant.CART_PREFIX + userInfoTo.getUserKey());
        if(cartItems!=null&&cartItems.size()>0){
            cart.setItems(cartItems);
        }
    }
    return cart;
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213131627498.png" alt="image-20231213131627498" style="zoom:50%;" />.

#### 拓展功能【了解】

##### 选中购物项

- 前端：给复选框绑定事件

```js
$(".itemChecked").click(function () {
   const skuId = $(this).attr("skuId");
   const checked = $(this).prop("checked");
   location.href = "http://cart.gulimall.com/checkItem?skuId=" + skuId + "&checked=" + (checked ? 1 : 0);
});
```

- 每次修改商品的选中状态就要调用后端方法？？？效率不会很低吗

```java
@GetMapping(value = "/checkItem")
public String checkItem(@RequestParam(value = "skuId") Long skuId,
                        @RequestParam(value = "checked") Integer checked) {
    cartService.checkItem(skuId,checked);
    return "redirect:http://cart.gulimall.com/cart.html";
}
public void checkItem(Long skuId, Integer checked) {
    CartItem cartItemBySkuId = getCartItemBySkuId(skuId.toString());
    cartItemBySkuId.setCheck(checked==1);
    BoundHashOperations<String, Object, Object> cartOps = getCartOps();
    cartOps.put(skuId.toString(),JSON.toJSONString(cartItemBySkuId));
}
```

##### 修改购物项数量

- 前端：给增减按钮绑定事件

```js
$(".countOpsBtn").click(function () {
   const skuId = $(this).parent().attr("skuId");
   const num = $(this).parent().find(".countOpsNum").text();
   location.href = "http://cart.gulimall.com/countItem?skuId=" + skuId + "&num=" + num;
});
```

- 和上一个一样，一修改就访问后端

```java
public void countItem(Long skuId, Integer num) {
    CartItem cartItemBySkuId = getCartItemBySkuId(skuId.toString());
    cartItemBySkuId.setCount(num);
    BoundHashOperations<String, Object, Object> cartOps = getCartOps();
    cartOps.put(skuId.toString(),JSON.toJSONString(cartItemBySkuId));
}
```

##### 删除购物项

- 前端

```js
let deleteId = 0;
$(".deleteItemBtn").click(function () {
    deleteId = $(this).attr("skuId");
});
//删除购物车选项
function deleteItem() {
    location.href = "http://cart.gulimall.com/deleteItem?skuId=" + deleteId;
}
```

- 后端

```java
public void deleteItem(Long skuId) {
    BoundHashOperations<String, Object, Object> cartOps = getCartOps();
    cartOps.delete(skuId.toString());
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213161207785.png" alt="image-20231213161207785" style="zoom: 50%;" /> ==》<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213161129389.png" alt="image-20231213161129389" style="zoom: 50%;" /> ==》<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213161146075.png" alt="image-20231213161146075" style="zoom: 50%;" />

### 订单服务【boss4】

#### 环境搭建

##### 页面导入

1. 导入订单详情、订单列表、订单确认、订单支付的所有静态资源

2. 配置域名映射，order.gulimall.com用于跳转订单服务，网关配置相关路由

   ```yaml
   - id: gulimall_order_route
     uri: lb://gulimall-order
     predicates:
       - Host=order.gulimall.com
   ```

3. 修改页面资源的引用路径

4. 测试各个页面的页面效果

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216164028594.png" alt="image-20231216164028594" style="zoom:50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216164048620.png" alt="image-20231216164048620" style="zoom:50%;" />

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216164321315.png" alt="image-20231216164321315" style="zoom:50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216165103791.png" alt="image-20231216165103791" style="zoom:50%;" />

##### 整合spring session

1. 引入redis和spring session的依赖
2. 配置session的存储类型、redis连接信息、线程池相关信息
3. session配置类中指定存储规则【json序列化和存储域】
4. 导入线程池配置类，后续异步要用到
5. 开启reids作为spring session的功能

#### 订单中心

- 电商系统涉及到 3 流，分别时信息流，资金流，物流，而订单系统作为中枢将三者有机的集合起来
- 订单模块是电商系统的**枢纽**，在订单这个环节上**需求获取多个模块的数据和信息**，同时对这些信息进行**加工处理后流向下个环节**，这一系列就构成了订单的信息流通

##### 订单构成

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231217151948795.png" alt="image-20231217151948795" style="zoom:67%;" />.

###### 用户信息 

- 用户信息包括用户账号、用户等级、用户的收货地址、收货人、收货人电话等组成，用户账 户需要绑定手机号码，但是用户绑定的手机号码不一定是收货信息上的电话

- 用户可以添加多个收货信息，用户等级信息可以用来和促销系统进行匹配，获取商品折扣，同时用户等级 还可以获取积分的奖励等 

###### 订单基础信息 

- 订单基础信息是订单流转的核心，其包括订单类型、父/子订单、订单编号、订单状态、订单流转的时间等
- 订单类型包括实体商品订单和虚拟订单商品等，这个根据商城商品和服务类型进行区分
- 同时订单都需要做父子订单处理，之前在初创公司一直只有一个订单，没有做父子订 单处理后期需要进行拆单的时候就比较麻烦，尤其是多商户商场，和不同仓库商品的时候， 父子订单就是为后期做拆单准备的
- 订单编号不多说了，需要强调的一点是父子订单都需要有订单编号，需要完善的时候 可以对订单编号的每个字段进行统一定义和诠释
- 订单状态记录订单每次流转过程，后面会对订单状态进行单独的说明
- 订单流转时间需要记录下单时间，支付时间，发货时间，结束时间/关闭时间等等 

###### 商品信息 

- 商品信息从商品库中获取商品的 SKU 信息、图片、名称、属性规格、商品单价、商户信息 等，从用户下单行为记录的用户下单数量，商品合计价格等

###### 优惠信息

- 优惠信息记录用户参与的优惠活动，包括优惠促销活动，比如满减、满赠、秒杀等，用户使 用的优惠券信息，优惠券满足条件的优惠券需要默认展示出来，具体方式已在之前的优惠券 篇章做过详细介绍，另外还虚拟币抵扣信息等进行记录
- 为什么把优惠信息单独拿出来而不放在支付信息里面呢？ 因为优惠信息只是记录用户使用的条目，而支付信息需要加入数据进行计算，所以做为区分

###### 支付信息 

- 支付流水单号，这个流水单号是在唤起网关支付后支付通道返回给电商业务平台的支 付流水号，财务通过订单号和流水单号与支付通道进行对账使用
- 支付方式用户使用的支付方式，比如微信支付、支付宝支付、钱包支付、快捷支付等
  -  支付方式有时候可能有两个——余额支付+第三方支付
- 各种金额
  - 商品总金额，每个商品加总后的金额
  - 运费，物流产生的费用
  - 优惠总金额，包括促销活动的优惠金额，优惠券优惠金额，虚拟积分或者虚拟币抵扣的金额，会员折扣的金额等之和
  - 实付金额，用户实际需要付款的金额
  - 用户实付金额=商品总金额+运费-优惠总金额

###### 物流信息

- 物流信息包括配送方式，物流公司，物流单号，物流状态，物流状态可以通过第三方接口来 获取和向用户展示物流每个状态节点

##### 订单状态

- 待付款 
  - 用户提交订单后，订单进行预下单，目前主流电商网站都会唤起支付，便于用户快速完成支付
  - 需要注意的是待付款状态下可以对库存进行锁定，锁定库存需要配置支付超时时间，超时后将自动取消订单，订单变更关闭状态
- 已付款/待发货 
  - 用户完成订单支付，订单系统需要记录支付时间，支付流水单号便于对账，订单下放到 WMS 系统，仓库进行调拨，配货，分拣，出库等操作
- 待收货/已发货 
  - 仓储将商品出库后，订单进入物流环节，订单系统需要同步物流信息，便于用户实时知悉物品物流状态
- 已完成 
  - 用户确认收货后，订单交易完成。后续支付侧进行结算，如果订单存在问题进入售后状态
- 已取消 
  - 付款之前取消订单。包括超时未付款或用户商户取消订单都会产生这种订单状态
- 售后中 
  - 用户在付款后申请退款，或商家发货后用户申请退换货
  - 售后也同样存在各种状态，当发起售后申请后生成售后订单，售后订单状态为待审核，等待商家审核，商家审核通过后订单状态变更为待退货，等待用户将商品寄回，商家收货后订单 状态更新为待退款状态，退款到用户原账户后订单状态更新为售后成功

#### 订单流程

- 订单流程是指从订单产生到完成整个流转的过程，从而行程了一套标准流程规则
- 而不同的产品类型或业务类型在系统中的流程会千差万别，比如上面提到的线上实物订单和虚拟订单的流程，线上实物订单与 O2O 订单等，所以需要根据不同的类型进行构建订单流程
- 不管类型如何订单都包括**正向流程和逆向流程**，对应的场景就是购买商品和退换货流程

##### 正向流程

- 就是一个正常的网购步骤：订单生成–>支付订单–>卖家发货–>确认收货–>交易成功。 而每个步骤的背后，订单是如何在多系统之间交互流转的，可概括如下

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240108142026664.png" alt="image-20240108142026664" style="zoom:80%;" />.

##### 订单创建与支付

- 订单创建前需要预览订单，选择收货信息等
- 订单创建需要锁定库存，库存有才可创建，否则不能创建 
- 订单创建后超时未支付需要解锁库存
- 支付成功后，需要进行拆单，根据商品打包方式，所在仓库，物流等进行拆单
- 支付的每笔流水都需要记录，以待查账
- 订单创建，支付成功等状态都需要给 MQ 发送消息，方便其他系统感知订阅

##### 逆向流程

- 修改订单，用户没有提交订单，可以对订单一些信息进行修改，比如配送信息， 优惠信息，及其他一些订单可修改范围的内容，此时只需对数据进行变更即可
- 订单取消，用户主动取消订单和用户超时未支付，两种情况下订单都会取消订 单，而超时情况是系统自动关闭订单，所以在订单支付的响应机制上面要做支付的 限时处理，尤其是在前面说的下单减库存的情形下面，可以保证快速的释放库存。 另外需要需要处理的是促销优惠中使用的优惠券，权益等视平台规则，进行相应补回给用户
- 退款，在待发货订单状态下取消订单时，分为缺货退款和用户申请退款。如果是 全部退款则订单更新为关闭状态，若只是做部分退款则订单仍需进行进行，同时生 成一条退款的售后订单，走退款流程【退款金额需原路返回用户的账户】
- 发货后的退款，发生在仓储货物配送，在配送过程中商品遗失，用户拒收，用户收货后对商品不满意，这样情况下用户发起退款的售后诉求后，需要商户进行退款的审核，双方达成一致后，系统更新退款状态，对订单进行退款操作，金额原路返回用户的账户，同时关闭原订单数据
  - 仅退款情况下暂不考虑仓库系统变化
  - 如果发生双方协调不一致情况下，可以申请平台客服介入
  - 在退款订单商户不处理的情况下，系统需要做限期判断，比如 5 天商户不处理，退款单自动变更同意退款

#### 订单确认业务

##### 购物车跳转结算

- 只有登录后的用户才能结算，整个订单服务都需要登录状态，可以声明一个拦截器来判断

```java
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberRespVo> loginUser=new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MemberRespVo attribute = (MemberRespVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if(attribute!=null){
            loginUser.set(attribute);//登陆成功就将用户信息存入当前线程
            return true;
        }else {
            //没登录就跳回登录页面
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    LoginUserInterceptor interceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**");
    }
}
```

##### 封装订单确认页数据

- 分析确认页信息

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231217161800609.png" alt="image-20231217161800609" style="zoom:67%;" />.

  - 收货人信息：有更多地址，即有多个收货地址，其中有一个默认收货地址
  - 送货清单：配送方式（不做）及商品列表（根据购物车选中的 skuId 到数据库中查询）
  - 优惠：查询用户领取的优惠券（不做）及可用积分（京豆）
  - 支付方式：货到付款、在线支付，不需要后台提供
  - 发票：不做

- 确认页vo

```java
@Data
public class OrderConfirmVo {
    List<MemberAddressVo> address;//用户所有收获地址信息
    List<OrderItemVo> items;//所有选中的购物项
    Integer integration;//会员的积分信息
    BigDecimal total;//订单总额
    BigDecimal payPrice;//应付价格
    String orderToken;//防重复提交
    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if(items!=null){
            for (OrderItemVo item : items) {
                sum=sum.add(item.getPrice().multiply(new BigDecimal(item.getCount())));
            }
        }
        return sum;
    }
    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
```

- 收货地址vo

```java
public class MemberAddressVo {
    private Long id;//地址的id
    private Long memberId;//会员id
    private String name;
    private String phone;
    private String postCode;//邮政编码
    private String province;//省份/直辖市
    private String city;//城市
    private String region;//区
    private String detailAddress;//详细地址(街道)
    private String areacode;//省市区代码
    private Integer defaultStatus;//是否默认
}
```

##### 获取确认页数据

###### 查询地址信息

- 会员服务中根据当前会员id查询所有地址

```java
@GetMapping("/{memberId}/addresses")
public List<MemberReceiveAddressEntity> getAddress(@PathVariable("memberId") Long memberId){
    List<MemberReceiveAddressEntity> addresses=memberReceiveAddressService.getAddresses(memberId);
    return addresses;
}
public List<MemberReceiveAddressEntity> getAddresses(Long memberId) {
    return this.list(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id",memberId));
}
```

- 远程接口方法

```java
@FeignClient("gulimall-member")
public interface MemberFeignService {
    @GetMapping("/gulimalmember/memberreceiveaddress/{memberId}/addresses")
    List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);
}
```

###### 获取被选中的购物项

- 购物车服务，有可能购物车价格为旧数据，所以需要查询每个购物项最新的价格

```java
@GetMapping("/currentUserCartItems")
public List<CartItem> getCurrentUserCartItems(){
    return cartService.getCurrentUserCartItems();
}
public List<CartItem> getCurrentUserCartItems() {
    UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
    if(userInfoTo.getUserId()==null){
        return null;
    }else {
        List<CartItem> cartItems = getCartItems(CartConstant.CART_PREFIX + userInfoTo.getUserId());
        List<CartItem> collect = cartItems.stream().//过滤所有被选中的购物项
                filter(cartItem -> cartItem.getCheck()).
                map(cartItem->{
                    cartItem.setPrice(productFeignService.getPrice(cartItem.getSkuId()));//更新最新的价格
                    return cartItem;
                }).
                collect(Collectors.toList());
        return collect;
    }
}
```

- 远程接口方法

```java
@FeignClient("gulimall-cart")
public interface CartFeignService {
    @GetMapping("/currentUserCartItems")
    public List<OrderItemVo> getCurrentUserCartItems();
}
```

###### 业务方法

```java
public OrderConfirmVo confirmOrder() {
    OrderConfirmVo confirmVo = new OrderConfirmVo();
    //1.远程查询当前用户的所有收获地址列表
    MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
    List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
    confirmVo.setAddress(address);
    //2.远程查询购物车所有选中的购物项
    List<OrderItemVo> cartItems = cartFeignService.getCurrentUserCartItems();
    confirmVo.setItems(cartItems);
    //3.查询用户积分
    Integer integration = memberRespVo.getIntegration();//当前登录的用户信息就有积分信息，直接获取
    confirmVo.setIntegration(integration);
    return confirmVo;
}
```

##### ！！！fegin远程调用的问题

###### 丢失头的问题

- 问题：在远程调用的过程中，cookie信息丢失，因为远程调用会**创建一个全新的请求模板**，其中没有原本的请求头信息

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231217172951118.png" alt="image-20231217172951118" style="zoom: 67%;" />.

- 解决：加上fegin远程调用的拦截器，来增强请求
  - RequestContextHolder可以获得本次请求的数据

```java
@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor(){
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //1.获取刚进来的请求的数据【老请求】
                ServletRequestAttributes attributes=
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attributes.getRequest();//获取到老请求
                //2.同步请求头数据
                //给新请求同步了老请求的Cookie头
                requestTemplate.header("Cookie",request.getHeader("Cookie"));
            }
        };
    }
}
```

###### 异步调用丢失上下文问题

- 问题：异步开启多线程，**老请求在原线程共享**，而异步任务不共享该请求信息

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231217180006517.png" alt="image-20231217180006517" style="zoom: 67%;" />.

- 解决：在异步任务执行之前，先获取原请求数据放在内存中共享，然后执行异步任务时就获取该请求数据并且存储
  - 主线程获取当前请求信息`RequestAttributes attributes = RequestContextHolder.getRequestAttributes()`
  - 异步线程中共享老请求信息`RequestContextHolder.setRequestAttributes(attributes);`

```java
@Override
public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
    OrderConfirmVo confirmVo = new OrderConfirmVo();
    MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();//获取老请求的信息
    //1.远程查询当前用户的所有收获地址列表
    CompletableFuture<Void> getAddresses = CompletableFuture.runAsync(() -> {
        RequestContextHolder.setRequestAttributes(attributes);//给新的异步线程共享老请求信息
        List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
        confirmVo.setAddress(address);
    });
    CompletableFuture<Void> getCartItems = CompletableFuture.runAsync(() -> {
        //2.远程查询购物车所有选中的购物项
        RequestContextHolder.setRequestAttributes(attributes);
        List<OrderItemVo> cartItems = cartFeignService.getCurrentUserCartItems();
        confirmVo.setItems(cartItems);
    });
    //3.查询用户积分
    Integer integration = memberRespVo.getIntegration();//当前登录的用户信息就有积分信息，直接获取
    confirmVo.setIntegration(integration);
    CompletableFuture.allOf(getAddresses,getCartItems).get();
    return confirmVo;
}
```

##### 优化

###### 查询库存信息

- 先给返回的数据添加Map<Long,Boolean> stocks属性来存储是否每个选中的商品是否有库存
  - key是商品id，value存储是否有库存
- 获取完购物项就可以根据商品的id来查询该商品是否有库存

```java
CompletableFuture<Void> getCartItems = CompletableFuture.runAsync(() -> {
    //远程查询购物车所有选中的购物项.....
}).thenRunAsync(()->{
    RequestContextHolder.setRequestAttributes(attributes);
    List<OrderItemVo> items = confirmVo.getItems();
    List<Long> skuIds = items.stream().map(item -> {
        return item.getSkuId();
    }).collect(Collectors.toList());
    R r = wmsFeignService.getSkusHasStock(skuIds);
    List<SkuHasStockVo> data = r.getData(new TypeReference<List<SkuHasStockVo>>() {
    });
    if(data!=null){
        Map<Long, Boolean> map = data.stream().//存储每个购物项的库存情况
                collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
        confirmVo.setStocks(map);
    }
},executor);
```

- 远程调用接口

```java
@FeignClient("gulimall-ware")
public interface WmsFeignService {
    @PostMapping("/gulimalware/waresku/hasStock")
    R getSkusHasStock(@RequestBody List<Long> skuIds);
} 
```

#### 订单提交【boss4-1】

##### 流程

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240107144641463.png" alt="image-20240107144641463" style="zoom: 80%;" />.

##### 订单确认业务优化

- 现在获取确认页信息的方法中加上创建防重令牌的功能

```java
//4.创建防重令牌
String token = UUID.randomUUID().toString().replace("-", "");
redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId(),token,
        30, TimeUnit.MINUTES);//服务器存储防重令牌
confirmVo.setOrderToken(token);//页面携带防重令牌
```

- 订单确认页完整业务代码

```java
public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
    OrderConfirmVo confirmVo = new OrderConfirmVo();
    MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();//获取老请求的信息
    //1.远程查询当前用户的所有收获地址列表
    CompletableFuture<Void> getAddresses = CompletableFuture.runAsync(() -> {
        RequestContextHolder.setRequestAttributes(attributes);//给新的异步线程共享老请求信息
        List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
        confirmVo.setAddress(address);
    });
    CompletableFuture<Void> getCartItems = CompletableFuture.runAsync(() -> {
        //2.远程查询购物车所有选中的购物项
        RequestContextHolder.setRequestAttributes(attributes);
        List<OrderItemVo> cartItems = cartFeignService.getCurrentUserCartItems();
        confirmVo.setItems(cartItems);
    }).thenRunAsync(()->{
        RequestContextHolder.setRequestAttributes(attributes);
        List<OrderItemVo> items = confirmVo.getItems();
        List<Long> skuIds = items.stream().map(item -> {
            return item.getSkuId();
        }).collect(Collectors.toList());
        R r = wmsFeignService.getSkusHasStock(skuIds);
        List<SkuHasStockVo> data = r.getData(new TypeReference<List<SkuHasStockVo>>() {
        });
        if(data!=null){
            Map<Long, Boolean> map = data.stream().//存储每个购物项的库存情况
                    collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
            confirmVo.setStocks(map);
        }
    },executor);
    //3.查询用户积分
    Integer integration = memberRespVo.getIntegration();//当前登录的用户信息就有积分信息，直接获取
    confirmVo.setIntegration(integration);
    CompletableFuture.allOf(getAddresses,getCartItems).get();
    //4.创建防重令牌
    String token = UUID.randomUUID().toString().replace("-", "");
    redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId(),token,
            30, TimeUnit.MINUTES);//服务器存储防重令牌
    confirmVo.setOrderToken(token);//页面携带防重令牌
    return confirmVo;
}
```

##### 封装订单提交数据

- 封装接收数据
  - 无需提交需要购买的商品，因为结算时需要去数据库**获取各个商品的最新数据**
  - 用户相关信息直接去session中获取就行了

```java
public class OrderSubmitVo {
    private Long attrId;//收货地址id
    private String payType;//支付方式
    private String token;//防重复令牌
    private BigDecimal payPrice;//应付价格，用于提醒用户购物车的商品价格是否发生了变化（验价）
    private String note;//订单备注（可做可不做）
}
```

- 封装返回数据

```java
public class SumbitOrderResponseVo {
    private OrderEntity order;
    private Integer code;//订单状态码
}
```

##### 验证令牌

- 令牌对比和删除操作需要保证原子性，应该使用接口幂等性中提到的lua脚本

```java
//1.验证令牌【保证幂等性】
String script="if redis.call('get', KEYS[1]) == ARGV[1] " +
        "then return redis.call('del', KEYS[1]) else return 0 end";
String orderToken = orderSubmitVo.getOrderToken();
Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),//lua脚本
        Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()),//使用到的key列表
        orderToken);//可变参数列表
if(result==0){
    //lua脚本返回值是0【校验失败】和1【成功】
    responseVo.setCode(1);
    return responseVo;
}else {
    //2.验证成功就创建订单
}
```

##### 创建订单

###### 封装订单数据

```java
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    private BigDecimal payPrice;
    private BigDecimal fare;
}
```

###### 抽取创建订单方法

```java
private OrderCreateTo createOrder() {
    OrderCreateTo createTo = new OrderCreateTo();
    //1、生成订单号
    String orderSn = IdWorker.getTimeId();
    OrderEntity orderEntity = builderOrder(orderSn);//屎山代码！！！！
    //2、获取到所有的订单项
    List<OrderItemEntity> orderItemEntities = builderOrderItems(orderSn);
    //3、更新价格、积分等信息
    computePrice(orderEntity,orderItemEntities);
    createTo.setOrder(orderEntity);
    createTo.setOrderItems(orderItemEntities);
    return createTo;
}
```

- 其中`builderOrder()`用于封装订单基本信息，那一块写的很乱，比如获取邮费都要另外进行远程调用，这里就不附上代码

- computePrice方法是用于重新计算用户订单项中的价格和积分信息，不附上代码

- 获取用户所有购物项数据builderOrderItems方法

  ```java
  private List<OrderItemEntity> builderOrderItems(String orderSn) {
      List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
      //最后确定每个购物项的价格
      List<OrderItemVo> currentCartItems = cartFeignService.getCurrentUserCartItems();
      if (currentCartItems != null && currentCartItems.size() > 0) {
          orderItemEntityList = currentCartItems.stream().map((items) -> {
              //构建订单项数据
              OrderItemEntity orderItemEntity = builderOrderItem(items);
              orderItemEntity.setOrderSn(orderSn);
              return orderItemEntity;
          }).collect(Collectors.toList());
      }
      return orderItemEntityList;
  }
  ```

  - 获取用户购物项时最终再确定一次购物金额，需要去数据库中获取商品最新价格

  - builderOrderItem单独构建每个购物项，**这一块我觉得不行**，每个购物项都要进行一次远程访问，还不如整体进行远程调用

    ```java
    private OrderItemEntity builderOrderItem(OrderItemVo items) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        //1、商品的spu信息
        Long skuId = items.getSkuId();
        //远程获取spu的信息...
        //2、商品的sku信息...
        //将list集合转换为String，并指定每个元素之间的分隔符
        String skuAttrValues = StringUtils.collectionToDelimitedString(items.getSkuAttr(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttrValues);
        //3、商品的优惠信息【不做】
        //4、商品的积分信息...
        //5、订单项的价格信息【各自优化价格默认为0】
        //当前订单项的实际金额.总额 - 各种优惠价格
        //原来的价格
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new 		                                                                 BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        //原价减去优惠价得到最终的价格
        BigDecimal subtract = origin.subtract(优惠券).subtract(促销);
        orderItemEntity.setRealAmount(subtract);
        return orderItemEntity;
    }
    ```

###### 保存订单信息

```java
private void saveOrder(OrderCreateTo order) {
    OrderEntity orderEntity = order.getOrder();
    this.save(orderEntity);//保存订单信息
    List<OrderItemEntity> orderItems = order.getOrderItems();
    orderItemService.saveBatch(orderItems);//批量保存订单项信息
}
```

##### 远程锁定库存

###### 流程

- 所有商品都锁定成功才算锁定完成，否则全部回滚
- 增强版逻辑：按照下单的收获地址找到就近仓库锁定库存【**不做**】

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240110135851401.png" alt="image-20240110135851401" style="zoom: 50%;" />.

###### 订单服务

- 封装锁定的库存信息

```java
public class WareSkuLockVo {
    private String orderSn;//订单号，为哪个订单号锁定库存
    private List<OrderItemVo> locks;//需要锁定的库存的信息
}
```

- 远程接口方法

```java
@PostMapping("/gulimalware/waresku/lock/order")
R orderLockStock(@RequestBody WareSkuLockVo vo);
```

- 业务逻辑

```java
R r = wmsFeignService.orderLockStock(lockVo);
if(r.getCode()==0){
    //锁定成功了
    responseVo.setOrder(order.getOrder());
    responseVo.setCode(0);
    return responseVo;
}else {
    throw new NoStockException(1l);
}
```

###### 库存服务

- 需要返回锁定的结果，即哪些商品锁定成功了

```java
public class LockStockResult {
    private Long skuId;//需要锁定的商品
    private Integer num;//锁定数量
    private boolean locked;//是否锁定成功
}
```

- 控制器方法

```java
@PostMapping("/lock/order")
public R orderLockStock(@RequestBody WareSkuLockVo vo){
    try {
        wareSkuService.orderLockStock(vo);
        return R.ok();
    } catch (Exception e) {
        e.printStackTrace();
        return R.error(BizCodeEnume.NO_STOCK_EXCEPTION.getCode(), BizCodeEnume.NO_STOCK_EXCEPTION.getMsg());
    }
}
```

- 业务方法

```java
@Transactional
@Override
public void orderLockStock(WareSkuLockVo vo) {
    //1.找到每个商品在哪个仓库有库存
    List<OrderItemVo> locks = vo.getLocks();
    List<SkuWareHasStock> lists = locks.stream().map(item -> {
        SkuWareHasStock skuWareHasStock = new SkuWareHasStock();//封装当前商品所有有库存的仓库信息
        Long skuId = item.getSkuId();
        skuWareHasStock.setSkuId(skuId);
        skuWareHasStock.setNum(item.getCount());
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id", skuId);//筛选存放过当前商品的仓库
        List<WareSkuEntity> wareList = this.baseMapper.
                selectList(queryWrapper.and(wrapper -> {
                    wrapper.gt("stock", 0);//查询哪些仓库是有当前商品的库存的
                }));
        skuWareHasStock.setWareId(wareList.stream().
                map(WareSkuEntity::getWareId).collect(Collectors.toList()));
        return skuWareHasStock;
    }).collect(Collectors.toList());
    //2.锁定库存
    for (SkuWareHasStock hasStockWare : lists) {
        Boolean isLocked=false;//当前商品的锁定情况
        Long skuId = hasStockWare.getSkuId();
        //遍历每个商品的库存情况
        List<Long> wareIds = hasStockWare.getWareId();
        if (wareIds == null || wareIds.size() == 0) {
            //当前商品在任何仓库都没有库存，一个没有，全体受罪
            throw new NoStockException(skuId);
        }
        for (Long wareId : wareIds) {
            //挨个仓库锁库存，如果所有仓库都锁定完，还是不够，那就直接抛异常
            Long count = this.baseMapper.lockSkuStock(skuId, wareId, hasStockWare.getNum());
            if(count>0){
                //锁定成功【锁定失败就交给下一个仓库继续重试】
                isLocked=true;
                break;//当前仓库锁定成功，就去锁定下一个商品
            }
        }
        if(!isLocked){
            //当前商品锁定失败，直接抛异常
            throw new NoStockException(skuId);
        }
    }
    //能走到这一步就说明所有商品都锁成功了,否则早抛异常了
}
```

- 自定义异常类

```java
public class NoStockException extends RuntimeException{
    public NoStockException(Long skuId){
        super("商品id位"+skuId+"没有足够的库存");
    }
}
```

- 锁定库存的sql，将这批商品的锁定任务只交给一个仓库锁定【无法做到一个仓库锁定一部分】，所以要求当前仓库的库存必须大于等于需要锁定的库存才可以锁定，否则交给下一个仓库

```sql
<update id="lockSkuStock">
    UPDATE wms_ware_sku SET stock_locked= stock_locked+#{num}
    WHERE sku_id=#{skuId} AND ware_id=#{wareId} AND stock-stock_locked>=#{num}
</update>
```

##### 订单提交总骨架

###### 控制器方法

```java
@PostMapping("/submitOrder")
public String sumbitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes attributes) {
    try {
        SumbitOrderResponseVo responseVo=orderService.sumbitOrder(orderSubmitVo);
        if(responseVo.getCode()==0){
            model.addAttribute("submitOrderResp",responseVo);
            return "pay";
        }else {
            //提交失败，携带错误原因
            String msg="";
            switch (responseVo.getCode()){
                case 1: msg="订单信息过期，请刷新之后再次提交";break;
                case 2: msg="购物车商品发生变化，请确认之后再次提交";break;
            }
            attributes.addFlashAttribute("msg",msg);
        }
    }catch (NoStockException e){
        attributes.addFlashAttribute("msg","库存锁定失败");
    }
    return "redirect:http://order.gulimall.com/toTrade";
}
```

###### 业务方法

```java
@Transactional
@Override
public SumbitOrderResponseVo sumbitOrder(OrderSubmitVo orderSubmitVo) {
    confirmVoThreadLocal.set(orderSubmitVo);//共享该vo对象，其它方法就不用传参数了
    MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();//从拦截器获取用户信息
    SumbitOrderResponseVo responseVo = new SumbitOrderResponseVo();//封装返回数据
    //创建订单、验证令牌、验价格、锁库存
    //1.验证令牌【保证幂等性】
    String script="if redis.call('get', KEYS[1]) == ARGV[1] " +
            "then return redis.call('del', KEYS[1]) else return 0 end";
    String orderToken = orderSubmitVo.getOrderToken();
    Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),//lua脚本
            Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()),//使用到的key列表
            orderToken);//可变参数列表
    if(result==0){
        //lua脚本返回值是0【校验失败】和1【成功】
        responseVo.setCode(1);
        return responseVo;
    }else {
        //2.验证成功就创建订单
        OrderCreateTo order = createOrder();
        BigDecimal payAmount = order.getOrder().getPayAmount();
        BigDecimal payPrice = orderSubmitVo.getPayPrice();
        //3.验价，创建订单的时候会给实体赋最新的价格信息，和前端传输的金额进行对比
        if(Math.abs(payAmount.subtract(payPrice).doubleValue())<0.01){
            //4.保存订单
            saveOrder(order);
            WareSkuLockVo lockVo=new WareSkuLockVo();
            lockVo.setOrderSn(order.getOrder().getOrderSn());
            order.getOrderItems().stream().map((item)->{
                OrderItemVo itemVo = new OrderItemVo();
                itemVo.setSkuId(item.getSkuId());
                itemVo.setCount(item.getSkuQuantity());
                itemVo.setTitle(item.getSkuName());
                return itemVo;
            }).collect(Collectors.toList());
            R r = wmsFeignService.orderLockStock(lockVo);
            if(r.getCode()==0){
                //锁定成功了
                responseVo.setOrder(order.getOrder());
                return responseVo;
            }else {
                throw new NoStockException(1l);
            }
        }else {
            responseVo.setCode(2);
            return responseVo;
        }
    }
}
```

##### seata分布式事务

###### 步骤

1. 环境搭建参照Seata的快速入门

2. 在提交订单方法加上@GlobalTransactional作为主分支

3. 在库存锁定执行成功之后模拟一个异常

4. 报错`service.vgroupMapping.default_tx_group configuration item is required`

   - 这个错误找了很久，最后是在各个需要分布式事务的服务中进行如下配置才解决的【和seataServer.properties统一配置】

     ```properties
     service.vgroupMapping.default_tx_group=default
     #If you use a registry, you can ignore it
     service.default.grouplist=127.0.0.1:8091
     ```

5. 测试结果全部事务进行回滚，包括远程调用的库存服务也回滚了

###### 分析

- seata分布式事务默认使用at模式【实际上也是二阶提交】
  - 一阶段：业务数据和回滚日志记录在同一个本地事务中提交，释放本地锁和连接资源
  - 二阶段
    - 提交异步化，非常快速地完成
    - 回滚通过一阶段的回滚日志进行反向补偿
- 因此seata分布式事务适用于**并发性不高**的后台管理服务，**订单服务其实不适用**

##### 消息队列引入

- seata的分布式事务不适用于高并发场景，因此订单服务最终采用可靠消息+最终一致性方案

###### 业务需求方案

- 订单服务执行失败就发送消息给库存服务，让其进行补救
- 库存服务本身也可以使用自动解锁模式

###### 库存解锁的场景

- 下订单成功，订单超时没有支付或者被用户手动取消，需要解锁库存
- 库存锁定成功之后订单服务异常回滚，之前锁定的库存需要解锁

###### 业务流程图

- 库存服务方面，锁定之前先将锁定记录存起来，如果锁定成功该记录会保存，锁定失败库存服务也会全部回滚
- 当订单服务为假失败时【即库存锁定成功但是订单服务后续出现异常】，库存服务就根据锁定记录来回滚/补救
- 此时就需要用延时队列来定期检查库存服务

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113153207065.png" alt="image-20240113153207065" style="zoom:75%;" />.

##### 库存服务使用消息队列

###### 库存服务环境搭建

1. 导入依赖，配置RabbitMQ服务器地址，@EnableRabbit开启RabbitMQ功能

2. 创建配置类，配置json序列化机制

3. 注入交换机、队列和绑定

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113175617239.png" alt="image-20240113175617239" style="zoom:67%;" />.

```java
@Bean
public Exchange stockEventExchange(){
    return new TopicExchange("stock-event-exchange",true,false);
}
@Bean
public Queue stockReleaseStockQueue(){
    return new Queue("stock.release.stock.queue",true,false,false);
}
@Bean
public Queue stockDelayQueue(){
    HashMap<String, Object> arguments = new HashMap<>();
    arguments.put("x-dead-letter-exchange", "stock-event-exchange");//死信路由
    arguments.put("x-dead-letter-routing-key", "stock.release");//死信的路由键
    arguments.put("x-message-ttl", 120000);//消息的过期时间
    return new Queue("stock.delay.queue",true,false,false,arguments);
}
@Bean
public Binding stockReleaseBinding(){
    return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
            "stock-event-exchange","stock.release.#",null);
}
@Bean
public Binding stockLockedBinding(){
    return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE,
            "stock-event-exchange","stock.locked",null);
}
```

4. 启动库存服务，查看队列、交换机和绑定的创建情况

   - 一开始没创建，因为RabbitMq会在**第一次连接上消息队列**的时候才创建【惰性创建】
   - 所以使用以下方法来监听消息队列，促使RabbitMQ创建组件【创建完就需要注释掉，免得抢消息】

   ```java
   @RabbitListener(queues = "stock.release.stock.queue")
   public void handle(Message message){
   }
   ```

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113180440522.png" alt="image-20240113180440522" style="zoom:67%;" />.

###### 库存锁定分析

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113152842921.png" alt="image-20240113152842921" style="zoom: 55%;" />.

- 锁定库存先需要将库存锁定情况保存到工作单，每件商品的锁定情况需要保存到详情

  - 如果每一个商品都锁定成功，各个商品对应的锁定情况都会发送给MQ
  - 锁定失败，前面保存的工作单信息就都回滚了，发送出去的消息因为在数据库中查不到，所以也不会执行解锁

- 库存锁定成功需要发消息给MQ【需要保存锁定的仓库、订单状态等】

  - 原先表设计没有考虑仓库和状态，所以需要加上

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113195135803.png" alt="image-20240113195135803" style="zoom:67%;" />.

  - 对应实体类要添加仓库id和锁定状态属性

    ```java
    private Long wareId;//在哪个仓库进行锁定
    private Integer lockStatus;//锁定状态
    ```

  - mapper映射文件也需要修改

    ```xml
    <result property="wareId" column="ware_id"/>
    <result property="lockStatus" column="lock_status"/>
    ```

- 封装给MQ发送的消息内容

  ```java
  public class StockLockedTo {
      private Long id;//库存工作单的id
      private Long detailId;//当前锁定的商品详情id
  }
  ```

- 库存锁定业务代码

```java
@Transactional
@Override
public void orderLockStock(WareSkuLockVo vo) {
    //0.保存库存工作单详情,为了追溯订单的行踪
    WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
    taskEntity.setOrderSn(vo.getOrderSn());
    orderTaskService.save(taskEntity);
    //1.找到每个商品在哪个仓库有库存
	...
    //2.锁定库存
    for (SkuWareHasStock hasStockWare : lists) {
		...
        for (Long wareId : wareIds) {
            //挨个仓库锁库存，如果所有仓库都锁定完，还是不够，那就直接抛异常
            Long count = this.baseMapper.lockSkuStock(skuId, wareId, hasStockWare.getNum());
            if(count>0){
				...
                StockLockedTo lockedTo = new StockLockedTo();
                lockedTo.setId(taskEntity.getId());
                lockedTo.setDetailId(detailEntity.getId());
                rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
                break;//当前仓库锁定成功，就去锁定下一个商品
            }
        }
		...
    }
}
```

###### 库存解锁分析

- 库存服务监听解锁队列，用消息中的工作单详情id去查询该商品的锁定情况
  - 如果有信息，说明库存锁定成功了，此时还不能直接解锁
    - 如果相关的订单不存在，必须解锁
    - 订单存在，得看订单状态，如果订单已取消就需要解锁
  - 没信息，说明库存服务自行回滚，无需解锁
- 所以如果库存锁定成功，需要远程查询订单的状态，没有数据说明订单不存在，有数据就根据具体状态执行操作

- 订单服务查询指定订单号方法

```java
@GetMapping("/status/{orderSn}")
public R getOrderStatus(@PathVariable("orderSn")String orderSn){
    Integer status=orderService.getOrderByOrderSn(orderSn);
    return R.ok().put("data",status);
}
public Integer getOrderByOrderSn(String orderSn) {
    OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    if(orderEntity!=null){
        return orderEntity.getStatus();
    }
    return null;
}
```

- 库存服务远程调用接口方法

```java
@FeignClient("gulimall-order")
public interface OrderFeignService {
    @GetMapping("/gulimalOrder/order/status/{orderSn}")
    R getOrderStatus(@PathVariable("orderSn")String orderSn);
}
```

- 库存服务解锁sql，解锁就是到原本的仓库中将锁定库存的数量减回去

```xml
<update id="unLockStock">
    update wms_ware_sku set stock_locked=stock_locked-#{num}
    where sku_id=#{skuId} and ware_id=#{wareId}
</update>
```

- 库存服务监听方法，为了保证消息队列的可靠性，需要手动签收`spring.rabbitmq.listener.simple.acknowledge-mode=manual`

```java
@Component //只有注入容器中的组件才可以监听
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {
    @Autowired
    WareSkuService wareSkuService;
    @RabbitHandler
    public void handleReleaseStock(StockLockedTo to, Message message, Channel channel) throws IOException {
        System.out.println("收到解锁库存的消息");
        try {
            wareSkuService.releaseStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);//处理成功就签收消息
        }catch (Exception e){
           //解锁期间出现异常都需要将当前的消息重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
```

- 库存服务解锁方法【我感觉应该先检查】

```java
public void releaseStock(StockLockedTo to) throws IOException {
    //库存自动解锁的情况
    Long id = to.getId();//库存工作单id
    Long detailId = to.getDetailId();//某件商品的锁定详情的id
    //1.查询该商品的锁定情况
    WareOrderTaskDetailEntity detailEntity = orderTaskDetailService.getById(detailId);
    if (detailEntity != null) {
        //有信息说明库存锁定成功了
        WareOrderTaskEntity taskEntity = orderTaskService.getById(id);
        String orderSn = taskEntity.getOrderSn();
        //远程查询订单状态
        R r = orderFeignService.getOrderStatus(orderSn);
        if (r.getCode() == 0) {
            Integer status = r.getData(new TypeReference<Integer>() {
            });
            if (status == null || status == 4) {
                //订单不存在或者订单已取消都需要解锁
                if(detailEntity.getLockStatus()==1){
                    //只有被锁定的库存才需要解锁
                    this.baseMapper.unLockStock(detailEntity.getSkuId(), detailEntity.getWareId(), detailEntity.getSkuNum(), detailId);
                    //解锁成功就需要将工作单的状态改为已解锁
                    WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
                    entity.setId(detailId);
                    entity.setLockStatus(2);
                    orderTaskDetailService.updateById(entity);
                }
            }
        }else {
            throw new RuntimeException("远程调用服务异常");
        }
    }
}
```

###### 报错分析

- 错误信息：无法解码成R类型

```java
org.springframework.amqp.rabbit.support.ListenerExecutionFailedException: Listener method 'public void com.liao.gulimal.gulimalware.service.impl.WareSkuServiceImpl.releaseStock(com.liao.common.to.mq.StockLockedTo,org.springframework.amqp.core.Message,com.rabbitmq.client.Channel) throws java.io.IOException' threw exception

Caused by: feign.codec.DecodeException: Could not extract response: no suitable HttpMessageConverter found for response type [class com.liao.common.utils.R] and content type [text/html;charset=UTF-8]
	at feign.InvocationContext.proceed(InvocationContext.java:40) ~[feign-core-11.10.jar:na]
	at feign.AsyncResponseHandler.decode(AsyncResponseHandler.java:116) ~[feign-core-11.10.jar:na]
	at feign.AsyncResponseHandler.handleResponse(AsyncResponseHandler.java:89) ~[feign-core-11.10.jar:na]
	at feign.SynchronousMethodHandler.executeAndDecode(SynchronousMethodHandler.java:141) ~[feign-core-11.10.jar:na]

Caused by: org.springframework.web.client.UnknownContentTypeException: Could not extract response: no suitable HttpMessageConverter found for response type [class com.liao.common.utils.R] and content type [text/html;charset=UTF-8]
```

- 断点调试查看远程服务执行完的response的请求体内容

  - 关键语句`response = this.client.execute(request, options)`

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113232617258.png" alt="image-20240113232617258" style="zoom:67%;" />.

  - 得出原因：同之前的feign远程调用丢失请求头一致，因为没有登录被登录请求拦截

- 解决：由于消息队列的监听不应该由用户来完成，而是需要通过后台来监听，所以在订单服务的拦截器中放行该请求

```java
String uri = request.getRequestURI();
boolean match = new AntPathMatcher().match("/gulimalOrder/order/status/**", uri);//匹配查询订单状态的请求就放行
if (match) {
    return true;
}
```

##### 订单服务使用消息队列

###### 订单超时关单

- 业务分析：订单如果超时未支付，则需要关闭订单，因此在订单创建成功的时候就需要放进延时队列中

```java
//订单创建成功需要发送消息到延时队列中
rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order);
```

- 监听方法

```java
@RabbitListener(queues = "order.release.order.queue")
@Component
public class OrderCloseLisener {
    @Autowired
    OrderService orderService;
    @RabbitHandler
    public void listerner(OrderEntity entity, Channel channel, Message message) throws IOException {
        System.out.println("收到过期的订单消息，准备关闭订单");
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);//签收消息
            orderService.closeOrder(entity);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
```

- 关单方法

```java
public void closeOrder(OrderEntity entity) {
    //先查询当前订单的最新状态
    OrderEntity orderEntity = this.getById(entity.getId());
    if(orderEntity!=null&&orderEntity.getStatus()
    ==OrderStatusEnum.CREATE_NEW.getCode()){
        //只有超时且尚未付款的订单需要关单
        orderEntity.setStatus(OrderStatusEnum.CANCLED.getCode());//更改订单状态为已取消
        this.updateById(orderEntity);
    }
}
```

- 报错：消息队列中存储的类型和客户端接收类型不一致，报以下错误【即没有处理】

  ```java
  Caused by: java.lang.NoSuchMethodException: No listener method found in com.liao.gulimal.gulimalOrder.listener.OrderCloseLisener for class com.liao.gulimal.gulimalOrder.to.RabbitHandler
  ```

  - 解决【提交订单方法中的order是OrderCreateTo，其属性order.getOrder才是orderEntity类型】

  ```java
  rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order.getOrder());
  ```

- 测试结果：超时未支付订单状态变成已取消，随后库存服务解锁库存

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240114150719990.png" alt="image-20240114150719990" style="zoom:67%;" />==》<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240114150608243.png" alt="image-20240114150608243" style="zoom:67%;" />==》<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240114150845274.png" alt="image-20240114150845274" style="zoom:67%;" />

###### 可能存在的问题

- 当前设计是订单服务消息延时一分钟，库存服务延时两分钟，不考虑网络延迟的话库存服务比订单服务后收到消息

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240114151242922.png" alt="image-20240114151242922" style="zoom: 50%;" />.

- 但是如果有网络延迟，库存服务可能就先收到消息，此时订单状态还没更新，就无法解锁

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240114151634737.png" alt="image-20240114151634737" style="zoom: 67%;" />.

- 解决：添加新的绑定关系，当订单超时取消的时候，就发消息给库存服务**主动解锁**

###### 主动补偿逻辑

- 添加绑定

```java
@Bean
public Binding orderReleaseOtherBingding() {
    //订单释放直接和库存释放进行绑定
    return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
            "order-event-exchange","stock.release.order.#",null);
}
```

- 当订单取消时，就向库存解锁队列发消息

```java
rabbitTemplate.convertAndSend("order-event-exchange","stock.release.order",orderEntity);
```

- 库存服务监听消息

```java
@RabbitHandler
public void handleOrderCloseRelease(OrderTo to, Message message, Channel channel) throws IOException {
    System.out.println("收到订单关闭的消息");
    try {
        wareSkuService.releaseStock(to);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);//处理成功就签收消息
    }catch (Exception e){
        //解锁期间出现异常都需要将当前的消息重新入队
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        log.error("解锁失败的原因{}",e.getMessage());
    }
}
```

- 库存服务的主动解锁代码【每次解锁成功一定要修改锁定状态，因为自动解锁也会解锁主动取消的订单】

```java
@Transactional //只要有一个解锁失败就全体回滚
@Override
public void releaseStock(OrderTo to) {
    //防止订单服务卡顿，导致订单状态为改变，无法解锁库存
    String orderSn = to.getOrderSn();
    //查询最新的库存状态，防止重复解锁库存
    WareOrderTaskEntity task=orderTaskService.getOrderTaskByOrderSn(orderSn);
    //根据库存工作单的id找到所有未解锁的商品
    List<WareOrderTaskDetailEntity> list = orderTaskDetailService.
            list(new QueryWrapper<WareOrderTaskDetailEntity>()
            .eq("task_id", task.getId())
            .eq("lock_status", 1));
    for (WareOrderTaskDetailEntity detailEntity : list) {
        this.baseMapper.unLockStock(detailEntity.getSkuId(), detailEntity.getWareId(),
                detailEntity.getSkuNum(), detailEntity.getId());
        //！！！完事后要把商品在工作单详情的锁定状态变成已解锁【否则自动解锁服务无法识别，就会重复解锁】
        detailEntity.setLockStatus(2);
        orderTaskDetailService.updateById(detailEntity);
    }
}
```

- 测试结果
  - 当订单过期的时候，会向库存解锁队列发起主动解锁，此时只要工作单详情中**未解锁**的商品就直接解锁，并且状态变成已解锁
  - 过一会库存服务过期消息到达，此时由于工作单详情中商品状态变为已解锁，就**不会重复解锁**

#### 订单支付

##### 环境搭建

- 执行支付时使用支付宝沙箱提供的用户来模拟支付
  - [支付宝开放平台 (alipay.com)](https://openhome.alipay.com/develop/sandbox/app)
  - 支付宝沙箱就是一个模拟支付的工具，会自己的应用提供一个买家账号
  - 应用的私钥用的也是沙箱环境提供的

1. 导入支付宝sdk

   ```xml
   <dependency>
       <groupId>com.alipay.sdk</groupId>
       <artifactId>alipay-sdk-java</artifactId>
       <version>4.38.192.ALL</version>
   </dependency>
   ```

2. 导入支付的模板和封装支付需要用到的数据【这一块老师有提供】

   ```java
   @ConfigurationProperties(prefix = "alipay")
   @Component
   @Data
   public class AlipayTemplate {
       private   String app_id;    //在支付宝创建的应用的id
       private  String merchant_private_key=...; // 商户私钥，您的PKCS8格式RSA2私钥【这里使用沙箱的私钥】
       // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
       private  String alipay_public_key=...;
       // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
       // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
       private  String notify_url;
       // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
       //同步通知，支付成功，一般跳转到成功页
       private  String return_url;
       private  String sign_type;// 签名方式    
       private  String charset;// 字符编码格式
       private  String gatewayUrl;// 支付宝网关； https://openapi.alipaydev.com/gateway.do
       public  String pay(PayVo vo) throws AlipayApiException {
           //1、根据支付宝的配置生成一个支付客户端
           AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                   app_id, merchant_private_key, "json",
                   charset, alipay_public_key, sign_type);
           //2、创建一个支付请求 //设置请求参数
           AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
           alipayRequest.setReturnUrl(return_url);
           alipayRequest.setNotifyUrl(notify_url);        
           String out_trade_no = vo.getOut_trade_no();//商户订单号，商户网站订单系统中唯一订单号，必填   
           String total_amount = vo.getTotal_amount();//付款金额，必填
           String subject = vo.getSubject(); //订单名称，必填        
           String body = vo.getBody();//商品描述，可空
           alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                   + "\"total_amount\":\""+ total_amount +"\","
                   + "\"subject\":\""+ subject +"\","
                   + "\"body\":\""+ body +"\","
                   + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
   
           String result = alipayClient.pageExecute(alipayRequest).getBody();
           //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
           System.out.println("支付宝的响应："+result);
           return result;
       }
   }
   ```

3. 配置个人应用信息

   ```properties
   alipay.app_id=9021000134606859 #个人应用的id
   alipay.notify_url=https://o442c90097.vicp.fun/order/pay/alipay/success 
   alipay.return_url=https://o442c90097.vicp.fun/pay/success.html 
   alipay.sign_type=RSA2
   alipay.charset=utf-8
   alipay.gatewayUrl=https://openapi-sandbox.dl.alipaydev.com/gateway.do #支付宝网关地址
   ```

4. 构建订单支付信息

   ```java
   public PayVo getOrderPay(String orderSn) {
       PayVo payVo = new PayVo();
       //先获取该订单编号对应的详细信息
       OrderEntity orderEntity = baseMapper.selectOne(
           new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
       BigDecimal bigDecimal =
               orderEntity.getPayAmount().setScale(2, BigDecimal.ROUND_UP);//支付金额精确到小数点后两位
       payVo.setTotal_amount(bigDecimal.toString());
       payVo.setOut_trade_no(orderEntity.getOrderSn());
       //以某个订单项的名字作为订单的标题
       List<OrderItemEntity> orderItemList =
               orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
       payVo.setBody(orderItemList.get(0).getSpuName());
       return payVo;
   }
   ```

5. 模板会生成一个根据封装的支付数据生成一个html，@ResponseBody默认响应json字符串，如果响应html需要用produces指定

   ```java
   @ResponseBody
   @GetMapping(value = "/aliPayOrder",produces = "text/html")//明确告诉浏览器本次返回一个html
   public String payOrder(@RequestParam("orderSn")String orderSn) throws AlipayApiException {
       PayVo payVo=orderService.getOrderPay(orderSn);
       String pay = alipayTemplate.pay(payVo);
       return pay;
   }
   ```

6. 报错：404，原因：配置文件中支付宝网关含有空格，所以访问不到

7. 测试成功

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240117191042499.png" alt="image-20240117191042499" style="zoom:50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240117191349619.png" alt="image-20240117191349619" style="zoom:67%;" />

##### 支付成功同步回调

###### 环境搭建

1. 配置中修改同步回调的地址，支付成功跳转到该会员的订单页`https://member.gulimall.com/memberOrder.html`

2. 会员服务导入相关静态资源，配置网关路由
3. 配置登录拦截器并配置拦截路径，还需要配置spring session

###### 分页展示用户订单

- 订单服务返回分页数据

  - 为了方便，直接在订单实体类中添加订单项集合字段，要声明成非数据库存在的字段

    ```java
    @TableField(exist = false)
    private List<OrderItemEntity> itemEntities;
    ```

  - 控制器方法

  ```java
  @GetMapping("listWithItem")
  public R listWithItem(@RequestParam Map<String,Object>params){
      PageUtils page = orderService.queryPageWithItem(params);
      return R.ok().put("page", page);
  }
  ```

  - 业务方法

  ```java
  public PageUtils queryPageWithItem(Map<String, Object> params) {
      MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
      //获取当前用户的订单
      IPage<OrderEntity> page = this.page(
              new Query<OrderEntity>().getPage(params),
              new QueryWrapper<OrderEntity>().eq("member_id",memberRespVo.getId()).orderByDesc("id")
      );
      for (OrderEntity entity : page.getRecords()) {
          List<OrderItemEntity> orderItemEntities = orderItemService.
                  list(new QueryWrapper<OrderItemEntity>().eq("order_sn", entity.getOrderSn()));
          entity.setItemEntities(orderItemEntities);
      }
      return new PageUtils(page);
  }
  ```

- 会员服务

  - 由于订单服务需要登录状态才可以访问，所以要配置fegin相关配置使得远程调用的新请求不会丢失cookie信息
  - 远程调用接口

  ```java
  @FeignClient("gulimall-order")
  public interface OrderFeginService {
      @GetMapping("/gulimalOrder/order/listWithItem")
      R listWithItem(@RequestParam Map<String,Object> params);
  }
  ```

  - 从订单服务中查询用户订单的分页数据，并返回

  ```java
  @GetMapping("/memberOrder.html")
  public String memberOrderPage(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, Model model) {
      Map<String,Object>param=new HashMap<>();
      param.put("page",pageNum);
      R r = orderFeginService.listWithItem(param);
      if(r.getCode()==0){
          model.addAttribute("orders",r);
      }
      return "orderList";
  }
  ```

- 前端照搬，显示结果如下

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240119150309276.png" alt="image-20240119150309276" style="zoom:50%;" />.

##### 异步回调

###### 支付宝异步回调理解

- 当订单支付成功之后，支付宝每隔一段时间会向应用发送post的异步请求来检查是否支付成功
- 因此支付宝的异步回调采用的是最大努力通知方案的分布式事务，直至超时或者收到success就不再通知

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240119151248859.png" alt="image-20240119151248859" style="zoom:67%;" />.

- 因此支付宝要通知到我们应用，就需要通过外网才可以访问得到，以下就是内网穿透的搭建过程

###### 内网穿透环境搭建

- 由于花生壳只能绑定IP地址无法绑定内网域名，所以直接配置订单服务的IP地址，如果配置内网域名会出现下述请求头不匹配问题
- 配置支付成功的异步回调地址`alipay.notify_url=https://o442c90097.vicp.fun/payed/notify`
- 测试监听方法

```java
@RestController
public class OrderPayedListener {
    @PostMapping("/payed/notify")
    public String handleAlipayed(HttpServletRequest request) {
        //收到支付宝的支付成功的异步通知，就需要返回success回应
        Map<String, String[]> parameterMap = request.getParameterMap();
        System.out.println("支付宝通知的数据"+parameterMap);
        return "success";
    }
}
```

- 由于回调功能的监听方法不需要登录，所以需要放行【这一块赶紧有点糙，每放行一个就需要添加一个boolean变量】

  `boolean match1 = antPathMatcher.match("/payed/notify", uri);`

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240119155454765.png" alt="image-20240119155454765" style="zoom:67%;" />.

###### 请求头不匹配问题

- 如果内网穿透配置的是内网域名，发送请求经过nginx时会出现请求头不匹配问题，请求携带的请求头是外网的域名，所以会不匹配

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240119153220481.png" alt="image-20240119153220481" style="zoom:67%;" />.

- 解决：nginx中配置精确的映射，将支付回调请求映射到订单服务所在的域名

  ```nginx
  location /payed/ {
      proxy_set_header Host order.gulimall.com;
      proxy_pass http://gulimall;
  }
  ```

  - 如果报404可以检查nginx的access和error日志

    - 这里可以看出请求携带的请求头是外网的

    ![image-20240119154155360](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240119154155360.png)

    - 错误原因是nginx没有监听到外网的域名信息，就默认去html中找映射，解决方式是监听外网的域名

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240119154552303.png" alt="image-20240119154552303" style="zoom:50%;" />.

###### 修改订单状态

1. 修改订单状态之前先必须先验签，验签操作直接cv

   ```java
   @PostMapping("/payed/notify")
   public String handleAlipayed(PayAsyncVo vo,HttpServletRequest request) {
       //收到支付宝的支付成功的异步通知，就需要返回success回应
       try {
           //修改订单状态之前必须先验签
           Map<String, String> params = new HashMap<>();
           Map<String, String[]> requestParams = request.getParameterMap();
           for (String name : requestParams.keySet()) {
               String[] values = requestParams.get(name);
               String valueStr = "";
               for (int i = 0; i < values.length; i++) {
                   valueStr = (i == values.length - 1) ? valueStr + values[i]
                           : valueStr + values[i] + ",";
               }
               //乱码解决，这段代码在出现乱码时使用
               // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
               params.put(name, valueStr);
           }
           boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(),
                   alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名
           if (signVerified) {
               System.out.println("签名验证成功...");
               //去修改订单状态
               orderService.handlePayResult(vo);
               return "success";
           } else {
               throw new RuntimeException("签名验证失败");
           }
       }catch (Exception e){
           return "fail";
       }
   }
   ```

2. 保存当前订单的流水，由于一个订单对应一条流水，所以流水中的订单号和支付交易号必须是唯一的

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240119162731648.png" alt="image-20240119162731648" style="zoom:67%;" />.

3. 支付宝发送的异步回调时间如果用Date类型接收，就需要声明日期类型的转换格式

   ```properties
   spring.mvc.format.date=yyyy-MM-dd HH:mm:ss
   ```

4. 修改订单状态方法

   ```java
   @Transactional
   @Override
   public void handlePayResult(PayAsyncVo vo) {
       //1.保存交易流水
       PaymentInfoEntity infoEntity = new PaymentInfoEntity();
       infoEntity.setAlipayTradeNo(vo.getTrade_no());
       infoEntity.setOrderSn(vo.getOut_trade_no());
       infoEntity.setPaymentStatus(vo.getTrade_status());
       infoEntity.setCallbackTime(vo.getNotify_time());
       paymentInfoService.save(infoEntity);
       //2.修改订单状态
       if (vo.getTrade_status().equals("TRADE_SUCCESS")||vo.getTrade_status().equals("TRADE_FINISHED")) {
           OrderEntity orderEntity = new OrderEntity();
           orderEntity.setStatus(OrderStatusEnum.PAYED.getCode());
           this.update(orderEntity,new UpdateWrapper<OrderEntity>().
                   eq("order_sn",infoEntity.getOrderSn()));
       }
   }
   ```

5. 支付宝沙箱不稳定，签名验证失败是正常现象，多试几次

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240119170952500.png" alt="image-20240119170952500" style="zoom:67%;" />.

##### 收单问题和解决

- 订单在支付页，不支付【此时一直刷新还是可以保持在支付页】，订单过期后才支付，订单状态改为已支付，但是库存已经解锁了

  -  使用支付宝自动收单功能解决，只要一段时间不支付，就不能支付了
  -  说人话就是加一个支付超时时间，对应的属性是`time_expire`
  -  收单涉及的属性可以参照[统一收单下单并支付页面接口 - 支付宝文档中心 (alipay.com)](https://opendocs.alipay.com/open/59da99d0_alipay.trade.page.pay?scene=22&pathHash=8e24911d)

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240121151029943.png" alt="image-20240121151029943" style="zoom:67%;" />.

- 由于时延等问题，订单解锁完成，正在解锁库存的时候，异步通知才到

  - 订单解锁时手动调用收单【参照支付宝的demo的alipay.trade.close.jsp】

- 网络阻塞问题，订单支付成功的异步通知一直不到达

  - 查询订单列表时，ajax获取当前未支付的订单状态，查询订单状态时，再获取一下支付宝此订单的状态 

- 其他各种问题

  - 每天晚上闲时下载支付宝对账单进行对账【啥呀这是】

#### 拓展

##### windows控制台检查端口占用

- netstart -ano：查出所有端口的占用情况
- netsart -ano | findstr 端口号：配合管道符查出指定端口的占用
- tasklist：查出当前系统的所有进程
- tasklist | findstr 端口号：查出指定端口被哪个进程占用了

##### 支付宝

###### 参照文档

- [支付宝开放平台 (alipay.com)](https://open.alipay.com/)

- [沙箱环境 - 支付宝文档中心 (alipay.com)](https://opendocs.alipay.com/common/02kkv7?pathHash=9a45a6d6)
- [产品介绍 - 支付宝文档中心 (alipay.com)](https://opendocs.alipay.com/open/repo-0038oa)
- [电脑支付SDK&demo下载 - 支付宝文档中心 (alipay.com)](https://opendocs.alipay.com/support/01rfv1)

###### 环境搭建

1. 登录支付宝开放平台，在个人控制台中创建应用

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240116152004120.png" alt="image-20240116152004120" style="zoom: 50%;" />.

2. 配置应用密钥消息

   - 应用密钥需要用支付宝提供的工具来生成

   - 本应用

     ```
     公钥
     MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAitsKWGQRiIhxE6bg56cYx9Qh9zJe0bpgJe6TVW1DcW5DaHCfSg1rB0enwEjGrYh/VNvsDKA1SKp4rN5V1ViPPnpUGRlbgzbBHTTLbuij7fsvSVviQPNV2yRYmTN1yylKpMq8n489Kmt3M4IB/cP3vhFQChiOUUxphZV02Z4GPyOFD1AGHeFgVVKVSiJCZyxudDXkHjnp7AlybthOIAobbMMmmJM3cwJg+rXhD7FN8GlIMDAwobTR/esGrKygCQra4UlSohYnymOoU5jVI+pQzVABNPLJyJN9sY2N9+//WItoC0622tSRMyTK2W2V8DL9NTRAQYoQU+MLHgBLAs5WWQIDAQAB
     私钥
     MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCK2wpYZBGIiHETpuDnpxjH1CH3Ml7RumAl7pNVbUNxbkNocJ9KDWsHR6fASMatiH9U2+wMoDVIqnis3lXVWI8+elQZGVuDNsEdNMtu6KPt+y9JW+JA81XbJFiZM3XLKUqkyryfjz0qa3czggH9w/e+EVAKGI5RTGmFlXTZngY/I4UPUAYd4WBVUpVKIkJnLG50NeQeOensCXJu2E4gChtswyaYkzdzAmD6teEPsU3waUgwMDChtNH96wasrKAJCtrhSVKiFifKY6hTmNUj6lDNUAE08snIk32xjY337/9Yi2gLTrba1JEzJMrZbZXwMv01NEBBihBT4wseAEsCzlZZAgMBAAECggEAIFYOPLNhGeicXLU6Hvhc3vxZxJVoW3MJvQuoJ/bABARnkkTX84jYaeOX/0FzcyocbQiiGfadMgTxAWDtoDd1dFlMiGPNWetAYarPzU5EDsG2K2FIeVOxpMz8DQFc5ykAhwvnjUjwN5a7NbWK/SvL3+lsOE2rpucik8dQZPVWAxHMRIuKUTDSSQFC7mNdjG9Mlju/GwhL1xyncmJ7NmFx0ss3vI4b6ZlSG+4PV1XY89sxPUWMeSd7iyUV05yckVJy4jBAvm0Aw4I3GL1twlakj5KIWPZKRebpNxKLsn864gbBL09j86NLy4GWPQOkcLGUHOgF122S00g7GpzWSJvcGQKBgQDQUVYX58DoZG0vB+0vCtnGhPx5a0VTZFIXg0UM5WY6pB3RkyUnc0yHHE22dRpw1bslXY0uz5O4T7ySCf7XUJDxRE533hxegKYqqJ8Ku1RBP5bha5/jQh/U98DDimAouCbkvKit8IWI3eIzYw7fLK06M4CtH836rbUivMjaFGRYNwKBgQCqo3oiVPARhha/qxJJvokNfX47yWidHYfPJPnZc9xm5vSHUAIh/DNj5S4+CbY5xFxDFmw6gQvJOSheA0m1kPC74KA6Ot8yYiVxrOmlYEsROjSUcGmf+MuAPABXExpD5x3XgpH8YoWF4y0GZLnfDGuukXMeILRdSr65quvPcYxd7wKBgQCsf22HBgUbam1u0GV62sVSObTG5ijV0PCZP73h2+M3E8cNT4coj7UC/FDtkTJ/1LvPSRQrev+bU4uWcmhv6uranfFMGMPtDSKLmG15+XzfOsS5jOEs0giB1VXtlZvim/q41e/neI9askEoxBIAg4I9+3/F29w4g7bGOQUocFJXKQKBgA3Asz1FHzwqVHcKMKUk6CLgNGZZK0dOc/2r+z8daWwZvSeSuTlH/FRWyk1RtxAc1VrK2do1QF/tbWV2WmB52A0sY32N9SM4adApoYMkqagvysET9k9gW/Zv348lCP7kB7Gw4lc3EY/i6WQUhi6F77/YFTEebar/NTP9pJfJjQidAoGAV+J7cqlPND6wpO2XRV6qu1PoO7GpkwnT6KNPJdbpmTs4qsE9Dcpae+svvfGVjOWz1HXabxnzbL1mYc7NTvTBEv0m7T0ZkPvO9t+mwrn9ji7C8xfWDBdRXGSMv5gRSWIZKkKpwDndDxq4HPhnwMoBe4RjrQ3Gj/qKL+WotZ1xYLg=
     ```

   - 支付宝公钥

     ```
     MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApjGR9aMsv9JzsYtx0HJX0itKf2zSi0XxpFmUL1HcVsOEEgdV92UP5CS2DfNyJe7/loxs1z4b1ZqgxPE/BlIGFt65yxrjaeS2JXT2hYEHWuTcEZVJ6uVVvup6HY7Uui08kEb6iWZmna7GZJOGup3zX8Cs8N32qLjimTgmEPArO9qUHRS9vY/Gba2nsQY3/9BQIzMnz7yc1q/tBiL6tWi/kXDyyX51bguozrjEws9dal2Hlnc/HvIeadkNXowcC8AFbVbpmGHQhTEVNavnMgBAliWjxHB6rZZNC03EGjK42OHFPflQTOEwDcPl5KNpU2xATVlX0OGPD/B373kBFEvb0QIDAQAB
     ```


###### 加密算法

- 对称加密：加密解密使用同一把钥匙（DES、3DES（TripleDES）、AES、RC2、RC4、RC5和Blowfish等）

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240116154008108.png" alt="image-20240116154008108" style="zoom:67%;" />.

- 非对称加密：加密解密使用不同钥匙（RSA、Elgamal等）
  - 发送方发送消息和解密用两把钥匙，接收方也是
  - 黑客有密钥A也无法对发送方的消息进行解密，如果有密钥B，可以解密，但是无法重新发回给发送方
  - 除非所有钥匙都知道，否则无法执行完整的通信流程

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240116154049321.png" alt="image-20240116154049321" style="zoom:67%;" />.

###### 支付宝重要概念

- 公钥私钥 
  - 公钥和私钥是一个相对概念，它们的公私性是相对于生成者来说的
  - 一对密钥生成后，保存在**生成者手里的就是私钥**， 生成者发布出去**大家用的就是公钥** 【暴露出去的是公钥】
- 加密
  - 加密是指我们使用一对公私钥中的一个密钥来对数据进行加密，而使用另一个密钥来进行解 密的技术
  - 公钥和私钥都可以用来加密，也都可以用来解密。  但这个加解密必须是一对密钥之间的互相加解密，否则不能成功
  - 加密是为了确保数据传输过程中的不可读性，就是不想让别人看到
- 数字签名
  - 给将要发送的数据，做上一个唯一签名（类似于指纹）
  - 用来互相验证接收方和发送方的身份，在验证身份的基础上再验证一下传递的数据是否被篡改过
  - 因此使用数字签名可以用来达到**数据的明文传输**
- 验签：支付宝为了验证请求的数据是否商户本人发的，商户为了验证响应的数据是否支付宝发的

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240116155100269.png" alt="image-20240116155100269" style="zoom:67%;" />.

##### 内网穿透

###### 引入

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240116163924380.png" alt="image-20240116163924380" style="zoom: 50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240116163939316.png" alt="image-20240116163939316" style="zoom:53%;" />

- 内网穿透服务商为给服务商软件临时分配一个域名
- 别人访问我们的软件时，先访问内网穿透服务商，服务商根据域名绑定关系找到我们的软件

###### 简介 

- 内网穿透功能可以允许我们使用外网的网址来访问主机
- 正常的外网需要访问我们项目的流程是
  1. 买服务器并且有公网固定 IP
  2. 买域名映射到服务器的 IP
  3. 域名需要进行备案和审核

###### 使用场景 

- 开发测试（微信、支付宝）
- 智慧互联
- 远程控制
- 私有云

###### 内网穿透的几个常用软件

- natapp：https://natapp.cn/ 优惠码：022B93FD（9 折）[仅限第一次使用]
- 续断：www.zhexi.tech 优惠码：SBQMEA（95 折）[仅限第一次使用]【没了】
- 花生壳：https://www.oray.com

###### 环境配置

1. 我使用的是花生壳，下载客户端之后添加映射

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240116170152076.png" alt="image-20240116170152076" style="zoom: 50%;" />.

2. 分配的应用域名地址`https://o442c90097.vicp.fun/`，之后就可以用这个域名来访问绑定的应用了

### 秒杀服务

#### 基础知识

##### 秒杀业务 

- 秒杀具有瞬间高并发的特点，针对这一特点，必须要做限流 + 异步 + 缓存（页面静态化） + **独立部署**
- 限流方式
  - 前端限流，一些高并发的网站直接在前端页面开始限流，例如：小米的验证码设计
  - nginx 限流，直接负载部分请求到错误的静态页面：令牌算法 漏斗算法
  - 网关限流，限流的过滤器
  - 代码中使用分布式信号量
  - rabbitmq 限流（能者多劳：chanel.basicQos(1)），保证发挥所有服务器的性能

##### 秒杀流程 

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240121164000652.png" alt="image-20240121164000652" style="zoom:67%;" />.

- 限流 参照 Alibaba Sentine

#### 环境搭建

##### 后台管理系统搭建

1. 配置优惠服务后台管理系统的网关

   ```yaml
   - id: coupon_route
     uri: lb://gulimal-coupon
     predicates:
       - Path=/api/coupon/**
     filters:
       - RewritePath=/api/coupon?(?<segment>.*),/gulimalcoupon/$\{segment}
   ```

2. 后台管理系统中新增今日秒杀场次

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240121155003650.png" alt="image-20240121155003650" style="zoom: 50%;" />.

3. 修改关联商品的方法【点击关联商品，直接查询到当前场次的所有商品】

   ```java
   public PageUtils queryPage(Map<String, Object> params) {
       QueryWrapper<SeckillSkuRelationEntity> queryWrapper = new QueryWrapper<>();
       String promotionSessionId = (String) params.get("promotionSessionId");
       if(!StringUtils.isEmpty(promotionSessionId)){
           queryWrapper.eq("promotion_session_id",promotionSessionId);
       }
       IPage<SeckillSkuRelationEntity> page = this.page(
               new Query<SeckillSkuRelationEntity>().getPage(params),
               queryWrapper
       );
       return new PageUtils(page);
   }
   ```

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240121160207340.png" alt="image-20240121160207340" style="zoom: 50%;" />.

##### 秒杀微服务搭建

1. 秒杀界面直接使用前端的就行了，不用模板引擎

2. 问题：搭建完服务后，发现该服务的Maven没有dependencies目录，并报错xxx依赖没有版本号

   - 添加springboot的场景依赖

   ```xml
   <parent>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-parent</artifactId>
       <version>2.7.17</version>
       <relativePath/> <!-- lookup parent from repository -->
   </parent>
   ```

   - 检查哪些依赖版本没引入，单独引进springCloud

   ```xml
   <dependencyManagement>
       <dependencies>
           <dependency>
               <groupId>org.springframework.cloud</groupId>
               <artifactId>spring-cloud-dependencies</artifactId>
               <version>2021.0.8</version>
               <type>pom</type>
               <scope>import</scope>
           </dependency>
       </dependencies>
   </dependencyManagement>
   ```

3. 配置redis、nacos、mysql等相关配置，开启服务注册发现

#### 定时上架

##### 定时任务配置类

- 创建一个定时任务配置类，开启定时任务功能和异步任务功能

##### 定时上架业务

- 业务需求：每天晚上3点，上架最近三天需要秒杀的商品

###### 远程调用相关

- 远程查询秒杀商品接口

```java
@FeignClient("gulimal-coupon")
public interface CouponFeignServcie {
    @GetMapping("/gulimalcoupon/seckillsession/lates3DaySesion")
     R getLateset3DaySession();
}
```

- 封装远程查询的秒杀活动信息

```java
public class SeckillSessionWithSkus {
    private Long id;
    private String name;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Date createTime;
    private List<SeckillSkuRelationEntity> relationSkus;
}
```

###### 业务总方法

```java
public void uploadSeckillSkuLatest3Day() {
    //1.扫描最近三天需要参与秒杀的活动
    R r = couponFeignServcie.getLateset3DaySession();
    if(r.getCode()==0){
        //2.上架商品
        List<SeckillSessionWithSkus> sessions = r.getData(
            new TypeReference<List<SeckillSessionWithSkus>>() {
        });
        //3.缓存到redis中，需要缓存活动信息和活动关联商品信息
        saveSessionInfos(sessions);//保存秒杀活动信息
        saveSessionSkuInfos(sessions);//保存秒杀商品信息
    }
}
```

###### 保存活动信息

- 保存每个时间场次以及关联的所有商品，其中时间场次作为key，关联的所有商品作为value

```java
private void saveSessionInfos(List<SeckillSessionWithSkus> sessions){
    for (SeckillSessionWithSkus session : sessions) {
        long startTime = session.getStartTime().getTime();
        long endTime = session.getEndTime().getTime();
        String key=SESSIONS_CACHE_PREFFIX+startTime+"_"+endTime;//活动范围作为redis的键
        List<String> collect = session.getRelationSkus().//获取当前活动的所有关联商品的id
                stream().map(skus->skus.getSkuId().toString()).collect(Collectors.toList());
        redisTemplate.opsForList().leftPushAll(key,collect);//缓存本次活动的所有商品
    }
}
```

###### 保存活动关联商品信息

- 每件商品以k-v的形式存入redis，所以需要有一个专门用来存储所有关联商品的Map

- 为了展示秒杀商品时方便，就给秒杀商品的添加商品详情`SkuInfo skuInfo;`字段，展示时不需要再去远程查询商品服务

- 调用商品服务查询当前关联商品的详情

  ```java
  @FeignClient("gulimall-product")
  public interface ProductFeignService {
      @RequestMapping("/gulimalProduct/skuinfo/info/{skuId}")
       R info(@PathVariable("skuId") Long skuId);
  }
  ```

- 每件关联商品需要知道时间范围，所以需要添加开始时间和结束时间字段

  ```java
  private Long startTime;
  private Long endTime;
  ```

- 添加随机码字段`String randomCode;`，为了防止连点器抢秒杀商品，所以加一个随机码，当秒杀开始才暴露出来

  - 只有知道秒杀商品的随机码才可以抢该商品
  - 如果没有设置随机码，外界就可以直接根据秒杀商品的id提前恶意攻击

- 在缓存中需要保存商品的秒杀数量，秒杀活动开始通过通过分布式信号量和随机码扣除缓存商品的秒杀库存

  - 需要导入redission分布式锁
  - 随机码作为分布式锁的key，关联的商品库存作为value
  - 采用信号量的作用是限流，而且只有秒杀请求的随机码正确才会访问redis扣减库存，也是一种限流

```java
private void saveSessionSkuInfos(List<SeckillSessionWithSkus> sessions){
    for (SeckillSessionWithSkus session : sessions) {
        //绑定一个专门用来存储秒杀活动关联商品的键
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFFIX);
        for (SeckillSkuRelationEntity relationSku  : session.getRelationSkus()) {
            //1.获取sku的基本信息
            R r = productFeignService.info(relationSku.getSkuId());
            if(r.getCode()==0){
                SkuInfo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfo>() {
                });
                relationSku.setSkuInfo(skuInfo);
            }
            //2.设置商品的开始时间和结束时间
            relationSku.setStartTime(session.getStartTime().getTime());
            relationSku.setEndTime(session.getEndTime().getTime());
            //3.设置秒杀商品的随机码【在秒杀开始的时候才暴露】
            String token = UUID.randomUUID().toString().replace("-", "");
            relationSku.setRandomCode(token);
            String jsonString = JSON.toJSONString(relationSku);//以json形式存储
			ops.put(relationSku.getSkuId().toString(),jsonString);
            //4.需要存储当前商品的库存，通过分布式信号量来扣减库存，使用随机码作为key才可以防止恶意攻击
            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
            semaphore.trySetPermits(relationSku.getSeckillSort());
        }
    }
}
```

###### 部署定时任务

```java
public class SeckillSkuScheduled {
    @Autowired
    SeckillService seckillService;
    @Scheduled(cron = "0 * * * * ?")
     public void uploadSeckillSkuLatest3Day(){
        //无需处理重复上架
        log.info("上架秒杀商品");
        seckillService.uploadSeckillSkuLatest3Day();
     }
}
```

##### 优惠服务查询秒杀商品

###### 控制器方法

```java
@GetMapping("lates3DaySesion")
public R getLateset3DaySession(){
   List<SeckillSessionEntity>sessions= seckillSessionService.getLates3DaySession();
   return R.ok().put("data",sessions);
}
```

###### 计算开始时间和结束时间

- LocalDateTime.of可以将年月日和时分秒拼接起来
- LocalTime.MIN是每日最小时间，为00:00:00
- LocalTime.MAX是每日最大时间，23:59:59
- LocalDate精确到日，LocalDateTime精确到秒
- plusDays方法可以在当前时间增加天数，类似的还有加月份等其他方法

```java
public String startTime(){
    //计算起始时间
    LocalDate now = LocalDate.now();//获取当前时间，精确到日
    LocalDateTime start=LocalDateTime.of(now, LocalTime.MIN);//LocalTime.MIN表示00：00:00
    String format = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    return format;
}
public String endTime(){
    //计算结束时间
    LocalDate now = LocalDate.now();
    LocalDate plusDays = now.plusDays(2);//当前时间+2天
    LocalDateTime end=LocalDateTime.of(plusDays, LocalTime.MAX);//LocalTime.MAX表示23：59：59
    String format = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    return format;
}
```

###### 业务方法

1. 先查出**最近三天**开始的所有秒杀活动，即今天、明天和后天开始的活动

2. 然后查出每个秒杀活动关联的商品【需要给秒杀活动实体类**添加关联商品的字段**，并标识为数据库中不存在】

   ```java
   @TableField(exist = false)
   private List<SeckillSkuRelationEntity> relationSkus;
   ```

```java
public List<SeckillSessionEntity> getLates3DaySession() {
    //1.查出最近三天开始的所有秒杀活动
    List<SeckillSessionEntity> list = this.list(new QueryWrapper<SeckillSessionEntity>().
            between("start_time", startTime(), endTime()));
    if(list!=null&&list.size()>0){
        //2.查出所有秒杀活动关联的商品
        List<SeckillSessionEntity> collect = list.stream().map(session -> {
            List<SeckillSkuRelationEntity> skus = seckillSkuRelationService.list(
                    new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", session.getId()));
            session.setRelationSkus(skus);
            return session;
        }).collect(Collectors.toList());
        return collect;
    }
    return null;//没有秒杀活动，或者所有秒杀活动都没有关联商品
}
```

##### 优化

###### 定时任务分布式下的问题

- 每轮上架只需要一个机器执行上架功能即可，所以需要加分布式锁

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240123141426228.png" alt="image-20240123141426228" style="zoom:67%;" />.

- 定时任务优化后的代码

```java
log.info("上架秒杀商品");
//获取分布式锁
RLock lock = redissonClient.getLock(upload_lock);
lock.lock(10, TimeUnit.SECONDS);
try {
    seckillService.uploadSeckillSkuLatest3Day();
}finally {
    lock.unlock();
}
```

###### 上架幂等性处理

- 已经上架过的秒杀活动和商品不需要重复上架，并且同一件商品的秒杀库存信号量不能重复设置
- 同一件商品可能出现在不同场次，所以需要保证一个场次中的同一件商品不能重复上架，所以需要将redis中秒杀商品的key改成sessionId_skuId的形式，就可以精确到哪个场次的哪件商品
- 秒杀活动幂等性处理

```java
if(!hasKey){
    List<String> collect = session.getRelationSkus().//获取当前活动的所有关联商品的id
            stream().map(skus -> skus.getPromotionSessionId()+"_"+skus.getSkuId().toString())
        .collect(Collectors.toList());
    redisTemplate.opsForList().leftPushAll(key,collect);//缓存活动信息
}
```

- 秒杀商品的幂等性处理

```java
if (!ops.hasKey(relationSku.getPromotionSessionId()+"_"+relationSku.getSkuId().toString())) {
    //1.获取sku的基本信息
	...
    //2.设置商品的开始时间和结束时间
	...
    //3.设置秒杀商品的随机码【在秒杀开始的时候才暴露】
	...
	ops.put(relationSku.getPromotionSessionId()+"_"+relationSku.getSkuId().toString(), jsonString);
    //4.需要存储当前商品的库存，通过分布式信号量来扣减库存，使用随机码作为key才可以防止恶意攻击
	...
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240123144327438.png" alt="image-20240123144327438" style="zoom:67%;" />.

#### 查询秒杀商品

##### 查询当前时间的秒杀商品

###### 控制器方法

```java
@GetMapping("/currentSeckillSkus")
public R getCurrentSeckillSkus(){
    List<SeckillSkuRelationEntity> skus=seckillService.getCurrentSeckillSkus();
    return R.ok().put("data",skus);
}
```

###### 业务方法

- 查询到的是当前时间可以参与的秒杀商品，所以随机码应该暴露出来

```java
public List<SeckillSkuRelationEntity> getCurrentSeckillSkus() {
    //1.确定当前时间属于哪个秒杀场次
    long curTime = new Date().getTime();
    //获取所有秒杀场次信息
    Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFFIX + "*");
    for (String key : keys) {
        String timeString = key.replace(SESSIONS_CACHE_PREFFIX, "");
        String[] split = timeString.split("_");//分隔出开始时间和结束时间
        Long startTime = Long.parseLong(split[0]);
        Long endTime = Long.parseLong(split[1]);
        if (curTime >= startTime && curTime <= endTime) {
            //2.获取当前秒杀场次的所有商品信息
            List<String> skus = redisTemplate.opsForList().range(key, 0, -1);//获取当前场次所有商品
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFFIX);
            List<Object> list = ops.multiGet(Collections.singleton(skus));
            if (list != null && list.size() > 0) {
                List<SeckillSkuRelationEntity> collect = list.stream().map(item -> {
                    SeckillSkuRelationEntity sku = JSON.parseObject((String) item, SeckillSkuRelationEntity.class);
                    return sku;
                }).collect(Collectors.toList());
                return collect;
            }
        }
    }
    return null;
}
```

###### 报错

- java.util.ArrayList cannot be cast to java.lang.String，报错位置：ops.multiGet(Collections.singleton(skus))

- 原因：multiGet()需要传入集合类型，skus本身就是集合类型，但是该方法需要接收集合的元素类型为redis存储map中的key的类型

  ```java
  List<HV> multiGet(Collection<HK> keys);
  public interface BoundHashOperations<H, HK, HV>
  ```

- 解决方式：在绑定redis键时指定map元素的key类型为String，然后multiGet直接传递skus

  ```java
  BoundHashOperations<String, String, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFFIX);
  List<Object> list = ops.multiGet(skus);
  ```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240123153830275.png" alt="image-20240123153830275" style="zoom:67%;" />.

##### 查询商品的秒杀预告

###### 商品服务

- 在查询商品详情业务方法中添加查询秒杀信息的功能
- 秒杀服务发送过来的秒杀商品信息中**和商品详情相关的不需要封装**，但是需要给商品详情实体类添加一个秒杀信息的属性

```java
public class SeckillSkuRelationVo {
   private Long id;
   private Long promotionId;
   private Long promotionSessionId;
   private BigDecimal seckillPrice;
   private Integer seckillCount;
   private BigDecimal seckillLimit;
   private Integer seckillSort;
   private Long startTime;
   private Long endTime;
   private String randomCode;//商品秒杀随机码
}
public class SkuItemVo {
	...
    //6.当前商品的秒杀信息
    SeckillSkuRelationVo seckillSkuRelationVo;
}
```

- 远程调用接口

```java
@FeignClient("gulimall-seckill")
public interface SeckillFeginService {
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId")Long skuId);
}
```

- 商品详情业务方法补充

```java
//查询当前商品是否参与秒杀优惠
CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
    R r = seckillFeginService.getSkuSeckillInfo(skuId);
    if (r.getCode() == 0) {
        SeckillSkuRelationVo skuRelationVo = r.getData(new TypeReference<SeckillSkuRelationVo>() {
        });
        skuItemVo.setSeckillSkuRelationVo(skuRelationVo);
    }
}, executor);
//等待所有任务都完成【可以不用等待infoFuture完成，因为有三个任务是基于infoFuture的，它们其中之一完成说明infoFuture早完成了】
CompletableFuture.allOf(imageFuture,descFuture,saleAttrsFuture,baseAttrsFuture,seckillFuture).get();
```

###### 秒杀服务

- 控制器方法

```java
@GetMapping("/sku/seckill/{skuId}")
public R getSkuSeckillInfo(@PathVariable("skuId")Long skuId){
    SeckillSkuRelationEntity relationEntity= seckillService.getSkuSeckillInfo(skuId);
    return R.ok().put("data",relationEntity);
}
```

- 业务方法
  - 根据skuId查询该商品是否参与秒杀，匹配所有的秒杀商品，如果当前商品存在，就返回秒杀时间范围
  - 由于是查询秒杀预告，如果当前商品不在秒杀时间范围就不能暴露秒杀随机码

```java
public SeckillSkuRelationEntity getSkuSeckillInfo(Long skuId) {
    //1.找到所有需要参与秒杀的key
    BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFFIX);
    Set<String> keys = ops.keys();
    if(keys!=null&&keys.size()>0){
        //2.由于redis存储的形式是sessionId_skuId,所以使用正则表达式匹配当前商品的skuId
        String regx="\\d_"+skuId;
        for (String key : keys) {
            if(Pattern.matches(regx,key)){
                String json = ops.get(key);
                SeckillSkuRelationEntity entity = JSON.parseObject(json, SeckillSkuRelationEntity.class);
                long currentTime=new Date().getTime();
                if(currentTime<entity.getStartTime()||currentTime>entity.getEndTime()){
                    entity.setRandomCode(null);//只要不在秒杀时间范围内就不该暴露随机码
                }
                return entity;
            }
        }
    }
    return null;
}
```

#### 秒杀抢购

##### 秒杀系统关注的问题

- [x] 服务单一职责+独立部署 
  - 秒杀服务即使自己扛不住压力挂掉，也不要影响别人 
- [x] 秒杀链接加密
  - 防止恶意攻击，模拟秒杀请求，1000次/s攻击
  - 防止链接暴露，自己工作人员，提前秒杀商品
  - 本项目使用随机码防止暴露
- [x] 库存预热+快速扣减 
  - 秒杀读多写少，无需每次实时校验库存
  - 库存预热，放到redis中，信号量控制进来秒杀的请求 
- [x] 动静分离
  - nginx做好动静分离，保证秒杀和商品详情页的动态请求才打到后端的服务集群
  - 使用CDN网络，分担本集群压力
- 恶意请求拦截
  - 识别非法攻击请求并进行拦截，网关层 
- 流量错峰
  - 使用各种手段，将流量分担到更大宽度的时间点
  - 比如
    - 输入验证码，不仅可以区分是机器还是人，不同用户输入验证码速度不同，可以将同一时刻的请求分散】
    - 加入购物车，加入购物车到最终消费还有一段流程，所以也可以分散请求
- 限流&熔断&降级
  - 前端限流+后端限流 
  - 限制次数，限制总量，快速失败降级运行，熔断隔离防止雪崩 
- 队列削峰
  - 1万个商品，每个1000件秒杀。双11所有秒杀成功的请求，进入队列，慢慢创建 订单，扣减库存即可

##### 秒杀流程

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240124160900027.png" alt="image-20240124160900027" style="zoom:67%;" />.

##### 登录限流

+ 前端登录限流可以通过判断session中是否有登录信息，没有就不能参与秒杀

- 秒杀服务的登录拦截器需要**拦截秒杀请求**，判断是否登录

```java
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(interceptor).addPathPatterns("/kill");//只有秒杀请求才需要判断登录
}
```

##### 快速下单

###### 控制器方法

```java
@GetMapping("/kill")
public R seckill(@RequestParam("killId") String killId, @RequestParam("key") String key,
                 @RequestParam("num") Integer num) {
    //能执行这个方法说明该用户已经登录了
    String orderSn=seckillService.kill(killId,key,num);//秒杀成功就返回一个订单号
    return R.ok().put("data",orderSn);
}
```

###### 封装秒杀订单消息

```java
public class SeckillOrderTo {
    private String orderSn;//订单号
    private Long promotionSessionId;//场次id
    private Long skuId;//商品id
    private BigDecimal seckillPrice;//秒杀价格
    private Integer num;//购买数量
    private Long memberId;//用户id
}
```

###### 业务方法

- 合法性判断成功之后就生成订单号，并且发送消息给队列【队列削峰】，只发送不监听可以不用标识@EnableRabbit

```java
public String kill(String killId, String key, Integer num) {
    MemberRespVo respVo = LoginUserInterceptor.loginUser.get();
    //1.获取当前秒杀商品的详细信息
    BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFFIX);
    String json = ops.get(killId);
    if (StringUtils.hasLength(json)) {
        return null;//没有这件秒杀商品
    } else {
        SeckillSkuRelationEntity entity = JSON.parseObject(json, SeckillSkuRelationEntity.class);
        //2.校验秒杀时间、随机码、秒杀商品id和购物数量的合法性
        long currentTime = new Date().getTime();
        if (currentTime >= entity.getStartTime() && currentTime <= entity.getEndTime()
                && entity.getRandomCode().equals(key)
                && killId.equals(entity.getPromotionSessionId() + "_" + entity.getSkuId())
                && num <= entity.getSeckillLimit()) {
            //3.验证当前用户是否已经购买过
            String isBuyedKey = respVo.getId() + "_" + killId;
            long ttl = entity.getEndTime() - currentTime;//需要设置过期时间，不能永久占位
            if (redisTemplate.opsForValue().setIfAbsent(isBuyedKey, num.toString(), ttl, TimeUnit.MILLISECONDS)) {
                //4.占位成功，说明当前用户没有购买过,获取信号量执行消费
                RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + key);
                try {
                 	//成功获取信号量才创建订单
                    if (semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS)) {
                        //快速下单,发送MQ消息
                        String orderSn = IdWorker.getTimeId().substring(0,32);
                        SeckillOrderTo orderTo = new SeckillOrderTo();
                        orderTo.setOrderSn(orderSn);
                        orderTo.setMemberId(respVo.getId());
                        orderTo.setNum(num);
                        orderTo.setPromotionSessionId(entity.getPromotionSessionId());
                        orderTo.setSeckillPrice(entity.getSeckillPrice());
                        orderTo.setSkuId(entity.getSkuId());
                        rabbitTemplate.convertAndSend("order-event-exchange","order.seckill.order",orderTo);
                        return orderSn;
                    }
                } catch (InterruptedException e) {
                    return null;
                }
            }
        }
    }
    return null;
}
```

##### 订单服务监听消息

###### 创建秒杀服务的队列和绑定

```java
@Bean
public Queue orderSeckillOrderQueue() {
    //秒杀服务的削峰队列
    Queue queue = new Queue("order.seckill.order.queue", true, false, false);
    return queue;
}
@Bean
public Binding orderSeckillBingding() {
    //削峰队列的绑定
    return new Binding("order.seckill.order.queue", Binding.DestinationType.QUEUE,
            "order-event-exchange","order.seckill.order",null);
}
```

###### 监听秒杀消息

```java
@RabbitHandler
public void listerner(SeckillOrderTo seckillOrderTo, Channel channel, Message message) throws IOException {
    try {
        log.info("准备创建秒杀单的详细消息");
        orderService.createSeckillOrder(seckillOrderTo);
        //手动调用支付宝收单
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);//签收消息
    }catch (Exception e){
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
    }
}
```

###### 创建订单【了解】

- 只是简单创建一下，远程调用等等都没有做

```java
public void createSeckillOrder(SeckillOrderTo seckillOrderTo) {
    //1.保存订单信息
    OrderEntity orderEntity = new OrderEntity();
    orderEntity.setOrderSn(seckillOrderTo.getOrderSn());
    orderEntity.setMemberId(seckillOrderTo.getMemberId());
    orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
    BigDecimal payPrice = seckillOrderTo.getSeckillPrice().multiply(new BigDecimal(seckillOrderTo.getNum()));
    orderEntity.setPayAmount(payPrice);//设置应付价格
    this.save(orderEntity);
    //2.保存订单项消息
    OrderItemEntity itemEntity = new OrderItemEntity();
    itemEntity.setOrderSn(seckillOrderTo.getOrderSn());
    itemEntity.setRealAmount(payPrice);
    orderItemService.save(itemEntity);
}
```

#### 拓展

##### 定时任务

###### cron表达式

- 来源：[Cron Trigger Tutorial (quartz-scheduler.org)](http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html)

- 语法：秒 分 时 日 月 周 年（**Spring不支持年**），数值范围和支持的特殊字符如下

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240121164334574.png" alt="image-20240121164334574" style="zoom: 67%;" />

- 特殊字符
  - ,：枚举； (cron="7,9,23 * * * * ?")：任意时刻的 7,9，23 秒启动这个任务
  - -：范围； (cron="7-20 * * * * ?")：任意时刻的 7-20 秒之间，每秒启动一次 
  - *：任意； 指定位置的任意时刻都可以 
  - /：步长
    - (cron="7/5 * * * * ?")：第 7 秒启动，每 5 秒一次
    - (cron="*/5 * * * * ?")：任意秒启动，每 5 秒一次
  - ？：出现在日和周几的位置，**为了防止日和周冲突**【比如1号不一定是周日】，在周和日上如果要写**通配符**就得使用? 
    - (cron="* * * 1 * ?")：每月的1号，启动这个任务 
  - L： last，最后一个，出现在日和周的位置
    - (cron="* * * ? * 3L")：每月的最后一个周二【因为1表示周日】 
  - W：Work Day，工作日 
    - (cron="* * * W * ?")：每个月的工作日触发 
    - (cron="* * * LW * ?")：每个月的最后一个工作日触发 
  - #：第几个 (cron="* * * ? * 5#2")：每个月的第2个周四
- 不需要特别记忆，网上有生成cron表达式的工具

###### SpringBoot整合

- @EnableScheduling 开启定时任务
- @Scheduled(cron = "* * * * * ?")定时执行标识的方法，语法是cron表达式
- 定时任务自动配置类TaskSchedulingConfiguration

```java
@Slf4j
@Controller //注入到容器中
@EnableScheduling
public class HelloSchedule {
    @Scheduled(cron = "* * * * * ?")
    public void hello(){
        log.info("hello");
    }
}
```

- spring cron语法的注意事项

  - spring cron语法不支持年份，所以只能有六位<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240121170256471.png" alt="image-20240121170256471" style="zoom:67%;" />

  - spring cron 周中**1是代表周一**【原本cron语法1是代表周日】
  - 其它语法就和cron官方的一样

###### 定时任务阻塞解决

- 定时任务默认阻塞，如果前一个定时任务阻塞了，后面的定时任务都无法执行

- 解决方式如下

  - 可以让业务以异步的方式运行，自己提交到线程池

  - 使用定时任务线程池【**不好使！**】，配置TaskSchedulingProperties`spring.task.scheduling.pool.size=5`

  - 可以采用异步任务让定时任务异步执行

    - @EnalbeAsync开启异步任务功能
    - @Async标注在需要异步执行的方法上

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240121171500475.png" alt="image-20240121171500475" style="zoom:67%;" />.

    - 异步任务不一定要标识在定时方法上，普通业务方法也可以标识，默认把任务提交给线程池
    - 自动配置类TaskExecutionAutoConfiguration

    ```properties
    spring.task.execution.pool.core-size=5
    spring.task.execution.pool.max-size=50
    ```

## 商城业务核心技术/知识

### nginx相关知识

#### 正向代理和反向代理

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121200303598.png" alt="image-20231121200303598" style="zoom:67%;" />.

##### 正向代理

- 在客户端部署代理服务器，**代替客户端对外部网络发送和接收消息**。客户端发送一个指定目标的请求给代理服务器，代理服务器再发送给目标服务器，目标服务器收到请求后，将响应的内容发送给代理服务器，代理服务器发给客户端

- 在正向代理的过程中，代理服务器代替客户端向目标服务器发送请求，**目标服务器不知道谁是真正的客户端**，不知道访问自己的是一个代理服务器还是客户端服务器只负责将响应包发送给请求方

##### 反向代理

- 服务器端部署代理服务器（为了区分，将真正响应的服务器成为业务服务器），让代理服务器替业务服务器接收请求或发送响应
- 客户端发送一个请求**给代理服务器**，代理服务器接收请求并将请求发送给业务服务器，业务服务器将响应发送给代理服务器，代理服务器再将响应发送给客户端

- 在反向代理的过程中，**客户端不知道自己请求的是代理服务器还是业务服务器**

#### 配置文件

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121200323764.png" alt="image-20231121200323764" style="zoom:67%;" />.

- nginx.conf中还会包含conf.d目录下的所有配置文件<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121200630149.png" alt="image-20231121200630149" style="zoom:67%;" />

### ！！！异步

#### 线程

##### 初始化线程的4种方式

- 4种方式

- 继承 Thread 

- 实现 Runnable 接口

- 实现Callable接口 + FutureTask （可以拿到返回结果，可以处理异常）

  - get功能：阻塞等待线程**完成**之后，获取返回结果

    ```java
    FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
    new Thread(futureTask).start();
    System.out.println(futureTask.get());
    public static class Callable01 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }
    }
    ```

- 线程池 【每个异步任务给线程池执行即可】

  ```java
  public static ExecutorService executor = Executors.newFixedThreadPool(10);//指定线程池中线程的数量
  
  service.execute(new Runable01());//普通执行
  Future<Integer> submit = service.submit(new Callable01());//执行完后可以获取返回值
  submit.get();
  ```

  - 或者原生创建方式【详情就看下方**线程池的七大参数**】

  ```java
  new ThreadPoolExecutor
      (corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit unit, workQueue, threadFactory, handler); 
  ```

- 比较

  - 方式1和方式2：主进程**无法获取线程的运算结果**，不适合当前场景 

  - 方式3：主进程可以获取线程的运算结果，但是**不利于控制服务器中的线程资源**，可能导致服务器资源耗尽

  - 方式4：通过如下两种方式初始化线程池 

    - 如果不使用线程池，一个异步任务就创建一个线程，容易导致资源耗尽，以后的业务中，**多线程异步任务都交给线程池执行**
    - 线程池**性能稳定**，也可以获取执行结果，并捕获异常
    - 但是在业务复杂情况下，一 个异步调用可能会依赖于另一个异步调用的执行结果

##### 线程池的七大参数

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231201190823280.png" alt="image-20231201190823280" style="zoom:67%;" />.

- corePoolSize： 核心线程数，池中**一直保持的线程的数量**
  - 即使线程空闲，只要线程池不销毁就一直存在，除非设置了allowCoreThreadTimeOut 
  - 在创建线程池时，会创建corePoolSize数量的空闲线程
- maximumPoolSize：池中允许的**最大的线程数**，用于控制资源 
- keepAliveTime：存活时间，作用是释放空闲线程
  - 当线程数大于核心线程数的时候，线程在存活时间到达的时候没有接到新任务就会终止释放
  - 最终线程池维持在corePoolSize大小【把超了核心大小且空闲的线程释放掉】
- unit：时间单位
- workQueue：阻塞队列，用来**存储等待执行的任务**
  - 如果当前对线程的需求超过了corePoolSize大小，就会放在这里等待空闲线程执行
  - 只要有线程空闲，就会去队列中取任务【所以人都忙着，新任务先堆积，有人闲下来就会去做】
  - 如果阻塞队列满了，还要新任务就创建新线程【人手不够了，再堆下去做不完了】
  - 阻塞队列有很多种，其中LinkedBLockingDeque<>()容量默认是Integer的最大值，容易导致内存不够，一定要指定容量
- threadFactory：创建线程的工厂，比如指定线程名等
- handler：**拒绝策略**，如果满了最大线程数，线程池就会使用拒绝策略拒绝执行任务
  - AbortPolicy()//默认丢弃策略，如果最大线程数满了，新任务就丢弃，而且会抛异常

```java
ExecutorService threadPool = new ThreadPoolExecutor(
        200,
        10,
        10L,
        TimeUnit.SECONDS,
        new LinkedBlockingDeque<Runnable>(10000),
        Executors.defaultThreadFactory(),
        new ThreadPoolExecutor.AbortPolicy()//默认丢弃策略，如果最大线程数满了，新任务就丢弃，而且会抛异常
);
```

##### 线程池工作顺序

1. 线程池创建，准备好corePoolSize数量的核心线程，准备接受任务 
2. 新的任务进来，先用corePoolSize准备好的空闲线程执行
   1. 核心线程都被占满了，就将再进来的任务放入阻塞队列中。核心线程空闲就会自己去阻塞队列获取任务执行
   2. 阻塞队列满了，就直接开新线程执行，最多只能开到maximumPoolSize指定的数量
   3. 任务都执行好了，（maximumPoolSize-corePoolSize）数量空闲的线程会在 keepAliveTime指定的时间后自动销毁。最终保持到corePoolSize大小
   4. 如果线程数达到maximumPoolSize的数量，还有新任务进来，就会使用reject指定的拒绝策略进行处理
3. 所有的线程创建都是由指定的factory创建的

##### 面试题

- 一个线程池 core 7； max 20 ，queue：50，100 并发进来怎么分配的
  - 先有7个能直接得到执行，接下来50个进入队列排队
  - 再在多开13个线程继续执行
  - 现在70个任务被安排上了，剩下30个任务使用拒绝策略

##### 常见的4种线程池

- newCachedThreadPool：创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程

  - corePoolSize=0，空闲线程都会回收

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231201193432107.png" alt="image-20231201193432107" style="zoom:67%;" />.

- newFixedThreadPool：创建一个定长线程池，可控制线程最大并发数，超出的任务会在队列中等待

  - corePoolSize=maximumPoolSize，空闲线程都不回收

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231201193500789.png" alt="image-20231201193500789" style="zoom:67%;" />.

- newScheduledThreadPool： 创建一个定时任务线程池，支持定时及周期性任务执行

- newSingleThreadExecutor：创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行

##### 开发中为什么使用线程池 

- 降低资源的消耗： 通过**重复利用已经创建好的线程**降低线程的创建和销毁带来的损耗
- 提高响应速度
  - 线程池中的线程数没有超过线程池的最大上限时，有的线程处于等待分配任务的状态，当任务来时无需创建新的线程就能执行
  - 减少线程等待时间片的时间，因为cpu只用在固定数量的线程中进行切换，而不是按任务数量进行切换
    - 没使用线程池时，假设有1000个任务，cpu就需要在1000个线程中进行切换
    - 使用线程池且指定最大线程数量为200，cpu只需要在200个线程中进行切换
- 提高线程的可管理性
  - 线程池会根据当前系统特点对池内的线程进行优化处理，减少创建和销毁线程带来的系统开销
  - 无限的创建和销毁线程不仅消耗系统资源，还降低系统的稳定性，使用线程池进行统一分配

#### CompletableFuture异步编排

##### 业务场景

- 查询商品详情页的逻辑比较复杂，有些数据还需要远程调用，必然需要花费更多的时间
- 假如商品详情页的每个查询，需要如下标注的时间才能完成，用户需要5.5s 后才能看到商品详情页的内容
  - 如果有多个线程同时完成这 6 步操作，也许只需要1.5s即可完成响应
  - 而且查询4、5、6需要1的结果，所以需要异步编排

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231201194849080.png" alt="image-20231201194849080" style="zoom:67%;" />.

##### Future和CompletableFuture

- Future用来描述一个异步计算的结果，提供了异步执行任务的能力

  - `isDone`方法检查计算是否完成，或者使用`get`阻塞住调用线程，直到计算完成返回结果
  - `cancel` 方法停止任务的执行
  - `Future`对于结果的获取很不方便
    - 只能**通过阻塞或者轮询**的方式得到任务的结果
    - 阻塞的方式显然和我们的异步编程的初衷相违背
    - 轮询的方式又会耗费无谓的 CPU 资源，而且也不能及时地得到计算结果

- Java 8新增加了CompletableFuture，提供了非常强大的Future的扩展功能

  - CompletableFuture 和FutureTask 同属于 Future 接口的实现类，都可以获取线程的执行结果

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231201200220771.png" alt="image-20231201200220771" style="zoom:67%;" />.

  - 可以帮助我们简化异步编程的复杂性，提供了函数式编程的能力，可以通过**回调**的方式处理计算结果，并且提供了转换和组合 CompletableFuture的方法

  - CompletableFuture 类实现了 Future 接口，所以可以通过`get`方法阻塞或者轮询的方式获得结果，但是这种方式不推荐使用

##### 创建异步对象

###### 方法

- CompletableFuture 提供了四个静态方法来创建一个异步操作
  - runXxxx 都是没有返回结果的，supplyXxx都是可以获取返回结果的【可以获得future对象进行异步编排】
  - 可以传入自定义的线程池，否则就用默认的线程池

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202134230103.png" alt="image-20231202134230103" style="zoom: 80%;" />.

###### 测试

```java
public static ExecutorService executor= Executors.newFixedThreadPool(10);
public static void main(String[] args) {
        System.out.println("方法开始");
//        CompletableFuture.runAsync(()->{
//            System.out.println("当前线程"+Thread.currentThread().getId());
//            System.out.println("运行结果"+10/2);
//        },executor);
        CompletableFuture<Integer> future = CompletableFuture
            .supplyAsync(() -> {
            int i = 10 / 2;
            System.out.println("当前线程"+Thread.currentThread().getId());
            System.out.println("运行结果" + i);
            return i;
        }, executor);
        try {
            Integer integer = future.get();
            System.out.println("方法结束"+integer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202134755081.png" alt="image-20231202134755081" style="zoom: 80%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202135226960.png" style="zoom: 80%;" />

##### 计算完成时回调方法

###### 方法

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202135515926.png" alt="image-20231202135515926" style="zoom:80%;" />.

- whenComplete 可以处理正常和异常的计算结果，exceptionally 处理异常情况
- whenComplete 和 whenCompleteAsync 的区别
  - whenComplete：是**执行当前任务的线程执行继续执行**whenComplete的任务
  - whenCompleteAsync：是执行把 whenCompleteAsync这个任务**继续提交给线程池**来进行执行【重新派发】
  - 方法不以 Async 结尾，意味着 Action使用相同的线程执行，而 Async可能会使用其他线程执行（如果是使用相同的线程池，也可能会被同一个线程选中执行）

###### 测试

- whenComplete会接收上一次任务执行的结果和异常信息
  - 如果上一次任务有异常，结果就为null
  - 顺序完成，异常就为null

```java
System.out.println("方法开始");
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
    int i = 10 / 2;
    System.out.println("当前线程"+Thread.currentThread().getId());
    System.out.println("运行结果" + i);
    return i;
}, executor).whenComplete((result,exception)->{
    System.out.println("异步任务成功,结果是"+result+"，异常是"+exception);
});
System.out.println("方法结束");
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202140158137.png" alt="image-20231202140158137" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202140248063.png" alt="image-20231202140248063" style="zoom: 80%;" />

- exceptionally 可以对异常进行处理

  - whenComplete虽然能得到异常信息，但是没法修改返回数据

  ```java
  System.out.println("方法开始");
  CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
      int i = 10 / 0;
      System.out.println("当前线程" + Thread.currentThread().getId());
      System.out.println("运行结果" + i);
      return i;
  }, executor).whenComplete((result, exception) -> {
      System.out.println("异步任务成功,结果是" + result + "，异常是" + exception);
  }).exceptionally(throwable ->{
      //可以感知异常，同时返回默认值
      return 10;
  });
  try {
      Integer integer = future.get();
      System.out.println("方法结束,返回结果"+integer);
  } catch (InterruptedException e) {
      e.printStackTrace();
  } catch (ExecutionException e) {
      e.printStackTrace();
  }
  ```

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202140816333.png" alt="image-20231202140816333" style="zoom:80%;" />.

##### handle方法

###### 方法

- 和complete一样，可对结果做最后的处理（可处理异常），可改变返回值

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202141039164.png" alt="image-20231202141039164" style="zoom:80%;" />.

###### 测试

```java
System.out.println("方法开始");
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
    int i = 10 / 0;
    System.out.println("当前线程" + Thread.currentThread().getId());
    System.out.println("运行结果" + i);
    return i;
}, executor).handle((result,exception)->{
    if(result!=null){
        return result*2;
    }
    if(exception!=null){
        return Integer.MIN_VALUE;
    }
    return 0;
});
try {
    Integer integer = future.get();
    System.out.println("方法结束,返回结果"+integer);
} catch (InterruptedException e) {
    e.printStackTrace();
} catch (ExecutionException e) {
    e.printStackTrace();
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202141342972.png" alt="image-20231202141342972" style="zoom: 80%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202141357398.png" alt="image-20231202141357398" style="zoom: 80%;" />

##### 线程串行化方法

###### 方法

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202141501482.png" alt="image-20231202141501482" style="zoom:80%;" />.

- thenApply 方法：当一个线程依赖另一个线程时，**获取上一个任务**返回的结果，并**返回当前任务的返回值**
- thenAccept 方法：消费处理结果【消费者只能消费，不能处理】。**接收上一个任务**的处理结果，并消费处理，**无返回结果**
- thenRun 方法：只要上面的任务执行完成，就开始执行thenRun，**不会接收**上一次的执行结果，而且**无返回结果**
- 和之前一样，带有Async默认是去线程池重新找线程，不带就是原本的线程继续执行，以上都要前置任务**成功完成**
- Function<? super T,? extends U> 
  - T：上一个任务返回结果的类型 
  - U：当前任务的返回值类型

###### 测试

- thenRun 无法感知上一次的执行结果，且无法返回

```java
System.out.println("方法开始");
CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
    int i = 10 / 4;
    System.out.println("当前线程" + Thread.currentThread().getId());
    System.out.println("运行结果" + i);
    return i;
}, executor);
CompletableFuture.supplyAsync(() -> {
    int i = 10 / 4;
    System.out.println("当前线程" + Thread.currentThread().getId());
    System.out.println("运行结果" + i);
    return i;
}, executor).thenRunAsync(()->{
    System.out.println("任务二启动了");
},executor);
```

- thenAccept 可以感知上一次的结果，但是无法返回

```java
CompletableFuture.supplyAsync(() -> {
    int i = 10 / 4;
    System.out.println("当前线程" + Thread.currentThread().getId());
    System.out.println("运行结果" + i);
    return i;
}, executor).thenAcceptAsync(result->{
    System.out.println("任务2启动了");
    System.out.println("任务1的结果为"+result);
},executor);
```

- thenApply可以感知上一次的结果，并且可以返回当前任务的结果

```java
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
    int i = 10 / 4;
    System.out.println("当前线程" + Thread.currentThread().getId());
    System.out.println("运行结果" + i);
    return i;
}, executor).thenApplyAsync(result -> {
    System.out.println("任务2启动了");
    System.out.println("任务1的结果为" + result);
    return 666;
}, executor);
System.out.println("任务2的结果"+future.get());
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202142403261.png" alt="image-20231202142403261" style="zoom: 80%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202142800443.png" alt="image-20231202142800443" style="zoom: 80%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202143045731.png" alt="image-20231202143045731" style="zoom:67%;" />

##### 两任务组合-都要完成

###### 方法

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202143401009.png" alt="image-20231202143401009" style="zoom: 80%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202143411686.png" alt="image-20231202143411686" style="zoom:80%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202143427767.png" alt="image-20231202143427767" style="zoom:80%;" />

- 组合的两个任务必须都完成才触发该任务 
- thenCombine：组合两个 future，获取两个 future 的返回结果，并返回当前任务的返回值 
- thenAcceptBoth：组合两个 future，获取两个 future 任务的返回结果，然后处理任务，没有 返回值
- runAfterBoth：组合两个 future，不需要获取 future 的结果，只需两个 future 处理完任务后， 处理该任务

###### 测试

- runAfterBoth无法感知

```java
CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
    int i = 10 / 4;
    System.out.println("任务1线程" + Thread.currentThread().getId());
    System.out.println("任务1结束" + i);
    return i;
}, executor);
CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
    int i = 10 / 4;
    System.out.println("任务2线程" + Thread.currentThread().getId());
    System.out.println("任务2结束" + i);
    return "hello";
}, executor);
future01.runAfterBothAsync(future02,()->{
    System.out.println("任务3开始");
},executor);
```

- thenAcceptBoth可以感知

```java
future01.thenAcceptBothAsync(future02,(f1,f2)->{
    System.out.println("任务3开始,之前的结果"+f1+"-->"+f2);
},executor);
```

- thenCombine可以感知，可以返回

```java
CompletableFuture<Integer> future03 = future01.thenCombineAsync(future02, (f1, f2) -> {
    System.out.println("任务3开始,之前的结果" + f1 + "-->" + f2);
    return 6666;
}, executor);
System.out.println(future03.get());
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202144507606.png" alt="image-20231202144507606" style="zoom:80%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202144423499.png" alt="image-20231202144423499" style="zoom:80%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202144729297.png" alt="image-20231202144729297" style="zoom:80%;" />

##### 两任务组合-一个完成

###### 方法

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202145211175.png" alt="image-20231202145211175" style="zoom:80%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202145222535.png" alt="image-20231202145222535" style="zoom:80%;" />

- 当两个任务中，任意一个 future 任务完成的时候，执行该任务
- applyToEither：两个任务有一个执行完成，获取它的返回值，处理任务并有新的返回值
- acceptEither：两个任务有一个执行完成，获取它的返回值【要求**两个任务的返回值类型相同**】，处理任务，没有新的返回值
- runAfterEither：两个任务有一个执行完成，不需要获取 future 的结果，处理任务，也没有返回值

###### 测试

- runAfterEither

```java
CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
    int i = 10 / 4;
    System.out.println("任务1线程" + Thread.currentThread().getId());
    System.out.println("任务1结束" + i);
    return i;
}, executor);
CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
    int i = 10 / 4;
    System.out.println("任务2线程" + Thread.currentThread().getId());
    try {
        Thread.sleep(3000);
        System.out.println("任务2结束" + i);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return "hello";
}, executor);
future01.runAfterEitherAsync(future02,()->{
    System.out.println("任务三完成");
},executor);
```

- acceptEither接收先完成的任务的返回值，要求**组合的任务返回值类型一致**

```java
CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
    int i = 10 / 4;
    System.out.println("任务1线程" + Thread.currentThread().getId());
    System.out.println("任务1结束" + i);
    return i;
}, executor);
CompletableFuture<Integer> future02 = CompletableFuture.supplyAsync(() -> {
    System.out.println("任务2线程" + Thread.currentThread().getId());
    try {
        Thread.sleep(3000);
        System.out.println("任务2结束");
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return 666;
}, executor);
future01.acceptEitherAsync(future02,(res)->{
    System.out.println("任务三开始之前的结果是"+res);
},executor);
```

- applyToEither接收先完成任务的返回值，而且返回当前任务的结果，而且组合任务调用get()不会被未完成的那个任务阻塞

```java
CompletableFuture<String> future = future01.applyToEitherAsync(future02, (res) -> {
    System.out.println("任务三开始之前的结果是" + res);
    return "hello";
}, executor);
System.out.println(future.get());
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202152311023.png" alt="image-20231202152311023" style="zoom:80%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202152616613.png" alt="image-20231202152616613" style="zoom:80%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202152906094.png" alt="image-20231202152906094" style="zoom:67%;" />

##### 多任务组合

###### 方法

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202153106243.png" alt="image-20231202153106243" style="zoom:80%;" />.

- allOf：等待所有任务完成 
- anyOf：只要有一个任务完成 

###### 测试

- allOf没有返回值
  - 调用get方法要阻塞等待所有任务执行完成
  - 不调用就直接执行

```java
        System.out.println("main start");
        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品图片信息");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("查到图片");
            return "hello.jpg";
        }, executor);
        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品属性");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("查到属性");
            return "黑色+256g";
        }, executor);
        CompletableFuture<String> futureBrand = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询品牌信息");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("查到品牌");
            return "华为";
        }, executor);
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futureImg, futureAttr, futureBrand);
//        allOf.get();//等待所有结果完成
        System.out.println("main end");
```

- anyOf有返回值，返回第一个执行完成的结果

```java
CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futureImg, futureAttr, futureBrand);
Object o = anyOf.get();//返回第一个完成的结果
System.out.println(o);
System.out.println("main end"+futureImg.get()+futureAttr.get()+futureBrand.get());
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202154558849.png" alt="image-20231202154558849" style="zoom:80%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231202155122079.png" alt="image-20231202155122079" style="zoom: 80%;" />

### spring session

#### 原理

- @EnableRedisHttpSession导入RedisHttpSessionConfiguration配置

- RedisHttpSessionConfiguration给容器中添加的核心组件

  - SessionRepository==》RedisOperationsSessionRepository：Redis操作session，session的增删改查封装类

  - SessionRepositoryFilter==》Filter：session存储过滤器，每个请求过来都必须经过filter

    1. Filter创建的时候，就自动从容器中获取到了SessionRepository

       <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231209165910998.png" alt="image-20231209165910998" style="zoom:67%;" />.

    2. 核心doFilterInternal方法

       - 先将SessionRepository存入当前请求对象

       - 然后采用**装饰模式**，先包装了原始请求和响应对象还有上下文环境，然后又包装了原始响应对象
       - 最后将包装后的对象应用到了整个执行链

       <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231209164915366.png" alt="image-20231209164915366" style="zoom: 50%;" />.

    3. 以后获取session实际调用的包装后的wrappedRequest.getSession()==》session是通过SessionRepository获取到的

#### 分布式session共享问题

##### session原理

- 访问哪个服务器，哪个服务器就会保存一个jsessionId作为用户标识，之后访问都会携带该jsessionId

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208173625004.png" alt="image-20231208173625004" style="zoom: 67%;" />.

##### session共享问题

- 不能跨不同域名共享访问某个服务，jsessionId只会存储在该服务，而**其它服务无法共享jsessionId**
- 同一种服务的jsessionId也是不共享的

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208174000078.png" alt="image-20231208174000078" style="zoom: 50%;" />.

##### 同一种服务共享解决

###### session复制

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208175001234.png" alt="image-20231208175001234" style="zoom:50%;" />.

- 优点
  - web-server（Tomcat）原生支持，只需要修改配置文件 
- 缺点
  - session同步需要数据传输，**占用大量网络带宽**，**降低了服务器群的业务处理能力**
  - 任意一台web-server保存的数据都是所有web- server的session总和，**<a>受到内存限制</a>无法水平扩展更多的web-server**
  - 大型分布式集群情况下，所有**web-server都全量保存数据**
- 此方案不可取

###### 客户端存储

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208174058103.png" alt="image-20231208174058103" style="zoom: 50%;" />.

- 优点
  - 服务器不需存储session，用户保存自己的 session信息到cookie中
  - 节省服务端资源
- 缺点
  - 都是缺点，这只是一种思路
  - 每次http请求，用户都要**携带cookie中的完整信息**， **浪费网络带宽**
  - session数据放在cookie中，cookie有**4K长度限制** ，不能保存大量信息
  - session数据放在cookie中，**存在泄漏、篡改、 窃取等安全隐患** 
- 这种方式不会使用

###### hash一致性

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208175535636.png" alt="image-20231208175535636" style="zoom:50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208175544974.png" alt="image-20231208175544974" style="zoom:50%;" />

-  优点
   - **只需要改nginx配置**，不需要修改应用代码
   - **负载均衡**，只要hash属性的值分布是均匀的，多台 web-server的负载是均衡的
   - 可以支持web-server**水平扩展**（session同步法是不行的，受内存限制）
-  缺点  
   - **session还是存在web-server**中的，所以**web-server重启可能导致部分session丢失**，影响业务，如部分用户需要重新登录
   - 如果web-server**水平扩展，rehash后session重新分布**， 也会有一部分用户路由不到正确的session
   - 但是以上缺点问题也不是很大，因为session本来都是有有效期的
-  可以使用

###### 统一存储

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208175921394.png" alt="image-20231208175921394" style="zoom: 50%;" />.

- 优点
  - 没有安全隐患
  - 可以水平扩展，数据库/缓存水平切分即可
  - web-server重启或者扩容都不会有 session丢失
- 不足
  - 增加了一次网络调用，并且需要修改应用代码，如将所有的getSession方法替换为从Redis查数据的方式
  - redis获取数据比内存慢很多
- 上面缺点**可以用SpringSession**完美解决

##### 不同服务的子域session共享解决

- 放大域名作用域【前提是不同服务需要部署到同一个父域名】
  - jsessionid这个cookie**默认是当前系统域名的**。当我们分拆服务，部署到不同域名的时候，可以使用如下解决方案
  - 即一开始获取到session中的jsessionid是当前系统域名，之后需要将jsessionid指定域名为父域名

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231208193302200.png" alt="image-20231208193302200" style="zoom: 50%;" />.

### ！！！消息队列

#### 消息队列基础概念

##### 应用场景

###### 异步处理

- 同步模式

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213162145275.png" alt="image-20231213162145275" style="zoom: 80%;" />.

- 异步处理：发送邮件和短信可以使用两个异步任务来完成

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213162232522.png" alt="image-20231213162232522" style="zoom:80%;" />.

- 写入消息队列：将发短信和邮件的操作交给消息队列完成，用户无需关注发送时间

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213162336950.png" alt="image-20231213162336950" style="zoom:80%;" />.

###### 应用解耦

- 传统做法：库存系统升级可能导致订单系统需要修改源代码且重新部署

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213163205285.png" alt="image-20231213163205285" style="zoom:80%;" />.

- 消息队列
  - 订单系统写入消息，库存系统不关心接口，通过分析消息的组成来订阅
  - 订单系统无需关心库存系统需要调用什么接口，只需要写消息即可

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213163236422.png" alt="image-20231213163236422" style="zoom: 80%;" />.

###### 流量控制

- 大并发请求先写入消息队列，然后写入成功就返回给各个用户
- 然后后台根据实际业务处理能力对消息进行消费

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213164259478.png" alt="image-20231213164259478" style="zoom:80%;" />.

##### 概述

###### 简介

- 大多应用中，可通过消息服务中间件来**提升系统异步通信、扩展解耦能力**
- 消息服务中两个重要概念：**消息代理**（message broker）和**目的地**（destination）
  - 当消息发送者发送消息以后，将由消息代理接管，消息代理保证消息传递到指定目的地
  - 消息代理可以理解成安装了消息中间件的服务器
- 市面的MQ产品：ActiveMQ、RabbitMQ、RocketMQ、Kafka

###### 两种形式的目的地

- **队列**（queue）【点对点式】：点对点消息通信（point-to-point）
  - 消息发送者发送消息，消息代理将其放入一个队列中，**消息接收者从队列中获取消息内容**，**消息读取后被移出队列**
  - 消息**只有唯一的发送者和接受者**，但并不是说只能有一个接收者，可以有很多人来访问队列，但**每条消息只会给一个人抢到**
- **主题**（topic）【发布订阅式】：发布（publish）/订阅（subscribe）消息通信
  - 发送者（发布者）发送消息到主题，**多个接收者（订阅者）监听（订阅）这个主题，那么就会在消息到达时<a>同时</a>收到消息**

###### 规范以及比较

- JMS（Java Message Service）JAVA消息服务：基于JVM消息代理的规范，ActiveMQ、HornetMQ是JMS实现
- AMQP（Advanced Message Queuing Protocol）
  - 高级消息队列协议，也是一个消息代理的规范，兼容JMS 
  - RabbitMQ是AMQP的实现

|              | JMS（Java Message Service）                                  | AMQP（Advanced Message Queuing Protocol）                    |
| ------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 定义         | Java api                                                     | 网络线级协议                                                 |
| 跨语言       | 否                                                           | 是                                                           |
| 跨平台       | 否                                                           | 是                                                           |
| Model        | 提供两种消息模型：Peer-2-Peer、Pub/sub                       | 提供了五种消息模型：direct exchange、fanout exchange、topic change、headers exchange、system exchange<br>本质来讲，后四种和JMS的pub/sub模型没有太大差别 仅是在路由机制上做了更详细的划分 |
| 支持消息类型 | 多种消息类型： TextMessage、 MapMessage、 BytesMessage 、StreamMessage 、ObjectMessage、Message （只有消息头和属性） | byte[]：当实际应用时，有复杂的消息，**可以将消息序列化后发送** |
| 综合评价     | JMS 定义了JAVA API层面的标准；在java体系中，多个client均可以通过JMS进行交互，不需要应用修改代码，但是其对跨平台的支持较差 | AMQP定义了wire-level层的协议标准；天然具有跨平 台、跨语言特性 |

###### Spring和SpringBoot支持

- spring-jms提供了对JMS的支持
- spring-rabbit提供了对AMQP的支持
- 需要ConnectionFactory的实现来连接消息代理
- 提供JmsTemplate、RabbitTemplate来发送消息
- @JmsListener（JMS）、@RabbitListener（AMQP）注解在方法上监听消息代理发布的消息
- @EnableJms、@EnableRabbit开启支持
- Spring Boot自动配置
  - JmsAutoConfiguration  
  - RabbitAutoConfiguration

#### RabbitMQ概念

##### 简介

- RabbitMQ是一个由erlang开发的AMQP(Advanved Message Queue Protocol)的开源实现

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213173230662.png" alt="image-20231213173230662" style="zoom: 50%;" />.

##### 核心概念

###### Message

- 消息，消息是不具名的，它**由消息头和消息体组成**
  - **消息体是不透明的**
  - 消息头则由一系列的可选属性组成， 这些属性包括routing-key（路由键）、priority（相对于其他消息的优先权）、delivery-mode（指出该消息可能需要持久性存储）等

###### Publisher

- 消息的生产者，也是一个向交换器**<a>发布</a>消息的客户端应用程序**

###### Exchange

- 交换器，用来**接收生产者发送的消息**并将这些消息**路由给服务器中的队列**
- Exchange有4种类型：direct(默认)，fanout, topic, 和headers，不同类型的Exchange转发消息的策略有所区别

###### Queue

- 消息队列，用来保存消息直到发送给消费者
  - 它是消息的容器，也是消息的终点
  - 一个消息可投入一个或多个队列
  - 消息一直在队列里面，等待消费者连接到这个队列将其取走

###### Binding

- 绑定，用**于消息队列和交换器之间的关联**
- 一个绑定就是**基于路由键**将交换器和消息队列连接起来的路由规则，所以可以将**交换器理解成一个由绑定构成的<a>路由表</a>**
- Exchange和Queue的绑定可以是**多对多**的关系

###### Connection

- 网络连接，比如一个TCP连接
- **一个客户端只会建立<a>一条</a>连接**来收发数据
- 消费者连接中断，mq会实时感知消费者下线并将消息存储起来

###### Channel

- 信道，**多路复用连接**中的一条**独立**的**双向数据流通道**
- 信道是建立在真实的TCP连接内的**虚拟连接**，AMQP 命令都是通过信道发出去的，不管是发布消息、订阅队列还是接收消息，这些动作都是通过信道完成【就是在一条连接中开辟多条道路】
- 因为对于操作系统来说建立和销毁TCP都是非常昂贵的开销，所以引入了信道的概念，以**复用一条TCP连接**

###### Consumer

- 消息的消费者，表示一个从消息队列中**<a>取得</a>消息的客户端应用程序**

###### Virtual Hot

- 虚拟主机，表示**一批交换器、消息队列和相关对象**，虚拟主机之间是隔离的
- 虚拟主机是**共享相同的<a>身份认证</a>和<a>加密环境</a>的独立服务器域**
- 每个 vhost 本质上就是一个 mini 版的RabbitMQ服务器，拥有自己的队列、交换器、绑定和权限机制
- vhost是AMQP概念的基础，**必须在连接时指定**，RabbitMQ 默认的 vhost 是 / 

###### Broker

- 表示**消息队列服务器实体**

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213173016173.png" alt="image-20231213173016173" style="zoom:67%;" />.

##### RabbitMQ运行机制

- AMQP 中的消息路由

  - AMQP 中消息的路由过程和 Java 开发者熟悉的 JMS 存在一些差别，AMQP 中增加了 Exchange 和Binding 的角色
  - 生产者**把消息发布到 Exchange 上**，消息最终到达队列并被消费者接收
  - 而 Binding 决定交换器的**消息应该发送到那个队列**

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214140535879.png" alt="image-20231214140535879" style="zoom:67%;" />.

##### Exchange类型

###### 分类

- Exchange分发消息时根据类型的不同分发策略有区别，目前共四种类型：direct、fanout、topic、headers 
- headers匹配 AMQP 消息的 header 而不是路由键，headers 交换器和 direct 交换器完全一致，但性能差很多，**目前几乎用不到**了

###### direct【完全匹配】

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214141606899.png" alt="image-20231214141606899" style="zoom:67%;" />.

- 消息中的路由键（routing key）如果和Binding 中的 binding key 一致， 交换器就将消息发到对应的队列中
- 路由键与队列名**完全匹配**，如果一个队列绑定到交换机要求路由键为“dog”，则只转发 routingkey 标记为“dog”的消息，不会转发
  “dog.puppy”，也不会转发“dog.guard”  等等
- 它是**完全匹配**、单播的模式

###### fanout【广播模式】

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214141715041.png" alt="image-20231214141715041" style="zoom:67%;" />.

- 每个发到 fanout 类型交换器的消息都会**分到<a>所有</a>绑定的队列**上去
- fanout 交换器**不处理路由键**，只是简单的将队列绑定到交换器上
- 每个发送到交换器的消息都会被转发到与该交换器**绑定的所有队列**上
- 很像子网广播，每台子网内的主机都获得了一份复制的消息
- fanout类型转发消息是最快的

###### topic【模糊匹配】

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214142001061.png" alt="image-20231214142001061" style="zoom: 80%;" />.

- topic交换器通过**模式匹配**分配消息的路由键属性，将路由键和某个模式进行匹配，此时队列需要绑定到一个模式上
- 它将路由键和绑定键的字符串**切分成单词**，这些**单词之间用点隔开**
- 它同样也会识别两个通配符：符号`#`和符号`*`，**#匹配0个或多个单词，*匹配一个单词**

#### 安装RabbitMQ

##### docker安装脚本

```dockerfile
docker run -d --name rabbitmq \
-p 5671:5671 -p 5672:5672 -p 4369:4369 -p  25672:25672 -p 15671:15671 -p 15672:15672 \
rabbitmq:management
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213174417889.png" alt="image-20231213174417889" style="zoom:67%;" />![image-20231213174600455](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213174600455.png)

##### 端口解释

- 官网：https://www.rabbitmq.com/networking.html

- 4369, 25672 (Erlang发现&集群端口)
- 5672, 5671 (AMQP端口)
- 15672 (web管理后台端口)
- 61613, 61614 (STOMP协议端口)
- 1883, 8883 (MQTT协议端口)

##### 测试

- 访问15672端口测试是否启动成功，默认账密都是guest

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213174703771.png" alt="image-20231213174703771" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231213174803217.png" alt="image-20231213174803217" style="zoom:67%;" />.

- 创建交换机

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214143026501.png" alt="image-20231214143026501" style="zoom:67%;" />.

- 创建队列

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214143409512.png" alt="image-20231214143409512" style="zoom:67%;" />.

- 交换机绑定【交换机可以绑定队列，也可以绑定交换机（多层路由）】

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214143635724.png" alt="image-20231214143635724" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214143647939.png" alt="image-20231214143647939" style="zoom:67%;" />

#### 测试不同类型交换机

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214151156219.png" alt="image-20231214151156219" style="zoom:50%;" />.

##### direct

1. 创建队列

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214151242633.png" alt="image-20231214151242633" style="zoom:67%;" />.

2. 创建direct类型的交换机并绑定队列

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214151526242.png" alt="image-20231214151526242" style="zoom:67%;" />.

3. 测试给direact交换机发布消息

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214151810167.png" alt="image-20231214151810167" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214151901448.png" alt="image-20231214151901448" style="zoom:67%;" />

4. 接受消息：可以指定接收消息的模式

   - Nack message requeue true接收消息但是将该消息重新放进队列
   - Automatic ack接收消息

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214152112775.png" alt="image-20231214152112775" style="zoom:67%;" />.

##### fanout

1. 创建fanout类型的交换机

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214152804291.png" alt="image-20231214152804291" style="zoom:67%;" />.

2. 添加消息队列

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214153001028.png" alt="image-20231214153001028" style="zoom:67%;" />.

3. 发布消息【不需要指定路由键】，即使指定了路由键，但是所有绑定的队列都会收到

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214153054853.png" alt="image-20231214153054853" style="zoom:67%;" />.

##### topic

1. 创建topic类型的交换机

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214153342554.png" alt="image-20231214153342554" style="zoom:67%;" />.

2. 分别绑定以liao开头和以news结尾的所有队列

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214153549629.png" alt="image-20231214153549629" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214153715377.png" alt="image-20231214153715377" style="zoom:67%;" />

3. 路由键指定liao.news，发送消息【全都收到】

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214153840297.png" alt="image-20231214153840297" style="zoom:67%;" />.

4. 发送给hello.news的路由键

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214154012919.png" alt="image-20231214154012919" style="zoom:67%;" />.

#### 整合RabbitMQ

##### 环境搭建

1. 在订单模块中引入spring-boot-starter-amqp

   - 引入启动场景，RabbitAutoConfiguration就会自动生效
   - 自动配置类
     - 给容器中自动配置了CachingConnectionFactory、 RabbitMessagingTemplate、RabbitTemplate、 AmgpAdmin
     - 所有的配置属性绑定在@ConfigurationProperties(prefix = "spring.rabbitmq")

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-amqp</artifactId>
   </dependency>
   ```

2. 配置连接信息

   ```properties
   spring.rabbitmq.host=192.168.32.100
   spring.rabbitmq.port=5672
   spring.rabbitmq.virtual-host=/
   ```

3. @EnableRabbit开启消息队列的监听功能


##### 创建交换机、队列和绑定

- AmgpAdmin可以创建交换机、队列和绑定
- 创建direct交换机

```java
DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
amqpAdmin.declareExchange(directExchange);
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214163912245.png" alt="image-20231214163912245" style="zoom:67%;" />.

- 创建队列<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214164722099.png" alt="image-20231214164722099" style="zoom: 80%;" />
  - exclusive指定该队列是否排他，即该队列只允许有一条连接

```java
Queue queue = new Queue("hello-java-queue",true,false,false);
amqpAdmin.declareQueue(queue);
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214165102288.png" alt="image-20231214165102288" style="zoom:67%;" />.

- 创建绑定需要指定目的地、目的地类型【交换机/队列】、交换机、路由键、自定义参数
  - 将交换机和指定的目的地进行绑定

```java
Binding binding = new Binding("hello-java-queue",Binding.DestinationType.QUEUE,
        "hello-java-exchange","hello.java",null);
amqpAdmin.declareBinding(binding);
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231214170317328.png" alt="image-20231214170317328" style="zoom:67%;" />.

##### 发送消息

###### 测试发送消息

- RabbitTemplate可以发送消息

- 需要指定发送给的交换机、路由键和消息内容

  ```java
  rabbitTemplate.convertAndSend("hello-java-exchange","hello.java","测试发送消息");
  ```

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216124516926.png" alt="image-20231216124516926" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216124849427.png" alt="image-20231216124849427" style="zoom:67%;" />

- 测试发送对象【默认采用java的序列化方式，要求发送的对象需要实现序列化接口】

  ```java
  OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
  reasonEntity.setId(1l);
  reasonEntity.setCreateTime(new Date());
  reasonEntity.setName("sdad");
  rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",reasonEntity);
  ```

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216124916308.png" alt="image-20231216124916308" style="zoom:67%;" />.

###### 自定义发送消息的序列化机制

```java
@Configuration
public class MyRabbitConfig {
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216125512139.png" alt="image-20231216125512139" style="zoom:67%;" />.

##### 接收/监听消息

###### 接收消息的类型

- 如果要监听消息就必须标识@EnableRabbit才可以开启监听功能，不需要监听的功能可以不标注

- @RabbitListener【可以标注在类、方法上】标注在业务逻辑的组件上，并且**该组件必须要在容器**中，才可以监听消息

  - 属性queues：声明需要监听的所有队列，数组类型
  - 监听消息的方法需要接收消息内容，消息类型可以指定
    - Message：原生消息类型，可以获取消息头和消息体
    - T：发送的消息类型，就不需要收到转换了
    - Channel ：当前传输数据的通道

  ```java
  @RabbitListener(queues = {"hello-java-queue"})
  public void recieveMessage(Object message){
      System.out.println("监听到了"+message);
  }
  ```

- 测试

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216131123259.png" alt="image-20231216131123259" style="zoom:67%;" />.

- 测试接收原生消息类型

  ```java
  @RabbitListener(queues = {"hello-java-queue"})
  public void recieveMessage(Message message){
      System.out.println("消息体"+message.getBody());
      System.out.println("消息头"+message.getMessageProperties());
      System.out.println("监听到了"+message);
  }
  ```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216131945003.png" alt="image-20231216131945003" style="zoom:67%;" />.

- 测试自动转换成实体类型

  ```java
  @RabbitListener(queues = {"hello-java-queue"})
  public void recieveMessage(OrderReturnReasonEntity message){
      System.out.println("监听到了"+message);
  }
  ```

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216132121726.png" alt="image-20231216132121726" style="zoom:67%;" />.

###### 多人监听消息

- 多人都可以监听同一个队列的消息，只要有一人接收到就删除该消息【每条消息只有一个接收者】

- 测试发送多条消息

  ```java
  OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
  reasonEntity.setCreateTime(new Date());
  reasonEntity.setName("sdad");
  for (long i = 0; i < 10; i++) {
      //模拟队列中有多条消息
      reasonEntity.setId(i);
      rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",reasonEntity);
  }
  ```

- 多个接收者同时监听<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216133031477.png" alt="image-20231216133031477" style="zoom:67%;" />

  - 同一个消息只能有一个人接收

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216133042864.png" alt="image-20231216133042864" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216133056580.png" alt="image-20231216133056580" style="zoom:67%;" />

  - 问题：总共发送了10条消息，客户端总共接收到了8条，原因是单元测试会启动一个客户端，参与接收消息

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216133416483.png" alt="image-20231216133416483" style="zoom:67%;" />.

- 消费者只有处理完当前消息，才能再去接收消息

###### @RabbitListener和@RabbitHander

- @RabbitListener可以标识在类或者方法上【监听哪些队列】，@RabbitHander只能标识在方法上 【重载区分不同的消息】
- @RabbitHander可以实现接收方法重载，能够将不同类型的消息定位到不同方法

- @RabbitListener标识在类上接收hello-java-queue队列的所有消息，使用@RabbitHandler接收处理不同的消息类型

```java
@RabbitListener(queues = {"hello-java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {
    @RabbitHandler
    public void recieveMessage(OrderReturnReasonEntity message) {
        System.out.println("监听到了OrderReturnReasonEntity类型的消息" + message);
    }
    @RabbitHandler
    public void recieveMessage2(OrderEntity message) {
        System.out.println("监听到了OrderEntity类型的消息" + message);
    }
}
```

- 发送不同类型的消息

```java
OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
reasonEntity.setCreateTime(new Date());
reasonEntity.setName("sdad");
OrderEntity orderEntity=new OrderEntity();
orderEntity.setCreateTime(new Date());
for (long i = 0; i < 10; i++) {
    //模拟队列中有多条消息
    if(i%2==0){
        reasonEntity.setId(i);
        rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",reasonEntity);
    }else {
        orderEntity.setId(i);
        rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderEntity);
    }
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216140350481.png" alt="image-20231216140350481" style="zoom:67%;" />.

#### RabbitMQ消息确认机制-可靠抵达

##### 概念

- 如果要保证消息不丢失，可靠抵达，可以使用事务消息，但是性能会下降250倍【官方说的】，为此引入确认机制
- publisher 
  - confirmCallback **确认**模式，确认消失**是否抵达服务器**
  - returnCallback  **未投递到queue<a>退回</a>**模式
- consumer **ack机制**

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216141226569.png" alt="image-20231216141226569" style="zoom: 50%;" />.

##### ConfirmCallback

+ 消息只要被 broker 接收到就会执行 confirmCallback

  + 如果是 cluster 模式，需要**所有broker**接收到才会调用confirmCallback

+ 被broker接收到只能表示message已经到达服务器，并不能保证消息一定会被投递到目标queue里

  + 所以需要用到接下来的returnCallback

+ 开启ConfirmCallback功能：新版是**spring.rabbitmq.publisher-confirm-type**

  + NONE值是禁用发布确认模式，是默认值
  + CORRELATED值是发布消息成功到交换器后会触发回调方法
  + SIMPLE值经测试有两种效果
    + 其一效果和CORRELATED值一样会触发回调方法
    + 其二在发布消息成功后使用rabbitTemplate调用waitForConfirms或waitForConfirmsOrDie方法等待broker节点返回发送结果，根据返回结果来判定下一步的逻辑，要注意的点是waitForConfirmsOrDie方法如果返回false则会关闭channel，则接下来无法发送消息到broker
  + ps：spring.rabbitmq.publisher-confirms=true在2.20版本之后被弃用了

+ 自定义ConfirmCallback方法【需要先开启ConfirmCallback功能，即spring.rabbitmq.publisher-confirm-type不为none】

  + @PostConstruct表示当前配置类生效再执行

  + CorrelationData：用来表示当前消息唯一性，相当于id，需要自己指定

    ```java
    rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",
            orderEntity,new CorrelationData(UUID.randomUUID().toString()));
    ```

  ```java
  @PostConstruct
  public void initRabbitTemplate(){
      rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
          @Override
          public void confirm(CorrelationData correlationData, boolean b, String s) {
              System.out.println("CorrelationData==>"+correlationData);//当前消息的唯一关联数据
              System.out.println("ack==>"+b);//是否成功,只要消息抵达服务器就成功
              System.out.println("failMessage==>"+s);//失败原因
          }
      });
  }
  ```

  + 循环依赖报错

    ```java
    Relying upon circular references is discouraged and they are prohibited by default. Update your application to remove the dependency cycle between beans. As a last resort, it may be possible to break the cycle automatically by setting spring.main.allow-circular-references to true.
    ```

    - 解决：spring.main.allow-circular-references=true允许循环依赖

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216150619264.png" alt="image-20231216150619264" style="zoom:67%;" />.

##### returnCallback  

- confrim模式只能保证消息到达broker，不能保证消息准确投递到目标queue里

- 有些业务场景需要保证消息一定要投递到目标queue里，此时就需要用到return退回模式

- 如果**未能投递到目标queue**里将调用returnCallback ，可以记录下详细到投递数据，定期的巡检或者自动纠错都需要这些数据

- 开启returnCallback功能

  - spring.rabbitmq.publisher-returns=true：开启发送端消息抵达队列确认
  - spring.rabbitmq.template.mandatory=true：只要抵达队列，就以异步模式优先回调

- 自定义returnCallback方法，只要消息没有投递到指定队列才触发

  ```java
  rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
      @Override
      public void returnedMessage(ReturnedMessage returnedMessage) {
          System.out.println(returnedMessage);
      }
  });
  ```

- 测试发送一个错误的路由键

  ```json
  ReturnedMessage [
      message=(Body:'[B@61901e73(byte[75])' MessageProperties [headers={__TypeId__=com.liao.gulimal.gulimalOrder.entity.OrderReturnReasonEntity},
  contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, deliveryTag=0]), #投递失败的消息内容
  replyCode=312, #失败状态码
  replyText=NO_ROUTE, #失败原因
  exchange=hello-java-exchange, routingKey=hello1.java]
  ```

##### ack机制

- 消费端确认，保证每个消息被正确消费后才被删除，queue无消费者，消息依然会被存储，直到消费者消费

- 消费者获取到消息，成功处理，可以回复Ack给Broker

  - basic.ack用于肯定确认；broker将移除此消息
  - basic.nack用于否定确认；可以指定broker是否丢弃此消息，可以批量
  - basic.reject用于否定确认；同上，但不能批量

- 消费者收到消息，**默认会自动ack**，只要消息抵达客户端就视为签收，但是**无法确定此消息是否被处理完成**

  - 问题举例：接收到很多消息并且都自动回复给服务器，但是只有部分数据处理成功就宕机了，其它消息都没处理完

- 保证消息不丢失只能手动确认

  - 消息处理成功，ack()，接受下一个消息，此消息broker就会移除
  - 消息处理失败，nack()/reject()，重新发送给其他人进行处理，或者容错处理后ack
  - 消息一直没有调用ack/nack方法，broker认为此消息正在被处理，不会投递给别人，此时客户端断开，消息不会被broker移除

- 开启手动确认收货：spring.rabbitmq.listener.simple.acknowledge-mode=manual

- 队列中的消息成功抵达客户端，但是如果没有签收，会以未签收状态重新入队，即使消费者宕机，消息也不会消失，下次有消费者来读取，队列中的消息会重新变为ready状态

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216152745432.png" alt="image-20231216152745432" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216153016345.png" alt="image-20231216153016345" style="zoom:67%;" />

- 签收货物需要用Channel接收，然后调用basicAck方法

  - 需要传递当前消息派发的标识【Message中有封装，该标识是**在当前通道内自增的**】和是否批量签收

  ```java
  public void recieveMessage(OrderReturnReasonEntity content, Message message,Channel channel) throws IOException {
      System.out.println("监听到了OrderReturnReasonEntity类型的消息" + content);
      channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
  }
  ```

- basicNack和basicReject都可以拒收，basicNack可以批量拒收

  - 以basicNack为例，有三个参数，分别是派发标识，是否批量拒收，是否重新入队
  - 重新入队的消息会被重新消费，相当于没有消费者调用ack的情况

  ```java
  System.out.println("监听到了OrderReturnReasonEntity类型的消息" + content);
  long deliveryTag = message.getMessageProperties().getDeliveryTag();
  if(deliveryTag%2==0){
      System.out.println("拒签");
      channel.basicNack(deliveryTag, false,true);
  }else {
      System.out.println("已经签收");
      channel.basicAck(deliveryTag,false);
  }
  ```

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231216155930173.png" alt="image-20231216155930173" style="zoom:67%;" />.

#### RabbitMQ延时队列

##### 场景

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113153826799.png" alt="image-20240113153826799" style="zoom: 50%;" />.

- 比如未付款订单，超过一定时间后，系统自动取消订单并释放占有物品

- **常用解决方案：**spring的 schedule 定时任务轮询数据库

  - 缺点

    - 消耗系统内存、增加了数据库的压力

    - 存在较大的时间误差，即定时任务的时效性问题

      <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113154542102.png" alt="image-20240113154542102" style="zoom: 50%;" />.

  - 解决：rabbitmq的消息TTL和死信Exchange结合，订单超时会发送消息给消息队列，此时才会给消息队列监听到

##### 核心概念

###### 消息的TTL

- 消息的TTL（Time To Live）就是**消息的存活时间**
- RabbitMQ可以对队列和消息分别设置TTL
  - 对队列设置就是队列**没有消费者连接**的保留时间
  - 也可以对每一个单独的消息做单独的设置，即没有消费该消息的保留时间
  - 超过了这个时间，认为这个消息/队列就死了，称之为**死信**
- 如果队列设置了，消息也设置了，那么会**取小的**
  - 一个消息如果被路由到不同的队列中，这个消息死亡的时间有可能不一样（不同的队列设置）
  - 单个消息的TTL才是实现延迟任务的关键
  - 可以通过设置**消息的expiration字段或者x-message-ttl属性**来设置时间，两者是一样的效果

###### Dead Letter Exchanges

-  一个消息在满足如下条件下【死信的情况】，会进**死信路由**，记住这里是路由而不是队列，一个路由可以对应很多队列

   - 一个消息被Consumer**拒收**了，并且reject方法的参数里**requeue是false**。不会被再次放在队列里让其他消费者使用

   - 上面的消息的TTL到了，消息过期了

   - 队列的长度限制满了，**排在前面**的消息会被丢弃或者扔到死信路由上

-  Dead Letter Exchange其实就是一种普通的exchange，和创建其他exchange没有两样

   - 只是在某一个设置Dead Letter Exchange的队列中有消息过期了，会自动触发消息的转发，发送到Dead Letter Exchange中去

-  既可以控制消息在一段时间后变成死信，又可以控制变成死信的消息被路由到指定的交换机，结合二者就可以实现一个延时队列

   - 即死信队列只用于存放消息，不能被监听，当队列中的消息超时了就丢到消费队列
   - 这样子消费队列中存储的一定是超时的消息

-  手动ack&异常消息统一放在一个队列处理建议的两种方式

   - catch异常后，**手动发送到指定队列**，然后使用channel给rabbitmq确认消息已消费

   - 给Queue绑定死信队列，使用nack（requque为false）确认消息消费失败

##### 延时队列实现

###### 实现方式

- 设置队列过期时间实现延时队列

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113160837714.png" alt="image-20240113160837714" style="zoom:67%;" />.

- 设置消息过期时间实现延时队列
  - 缺点：MQ采用惰性检查机制，放进定时队列的消息假设有三条，过期时间分别为5min、3min、1s，服务器弹出第一条数据，发现过期时间为5min，于是五分钟之后再重新检查，时间短的消息如果前面有时间长的消息阻塞着，就会有影响

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113161210236.png" alt="image-20240113161210236" style="zoom: 67%;" />.

###### 业务设计

- 简单版

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113162109520.png" alt="image-20240113162109520" style="zoom:67%;" />.

- 升级版

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113163631880.png" alt="image-20240113163631880" style="zoom:67%;" />.

###### 环境搭建

- springboot中Queue、Exchange、Binding可以使用@Bean注入到容器
  - 前提是MQ中没有创建这些组件，如果MQ中有这些组件，即使@Bean声明的属性发生变化也**不会覆盖**
  - 修改组件属性只能先删除MQ的组件，再@Bean注入

```java
@Configuration
public class MyMQConfig {
    @Bean
    public Queue orderDelayQueue() {
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order-event-exchange");//死信路由
        arguments.put("x-dead-letter-routing-key", "order.release.order");//死信的路由键
        arguments.put("x-message-ttl", 60000);//消息的过期时间
        //延时/死信队列
        Queue queue = new Queue("order.delay.queue", true, false, false, arguments);
        return queue;
    }
    @Bean
    public Queue orderReleaseQueue() {
        //消费队列
        Queue queue = new Queue("order.release.order.queue", true, false, false);
        return queue;
    }
    @Bean
    public Exchange orderEventExchange() {
        //topic类型的交换机,支持模糊匹配
        return new TopicExchange("order-event-exchange", true, false);
    }
    @Bean
    public Binding orderCreateOrderBingding() {
        //和order-event-exchange进行绑定，将路由键为order.create.order的消息路由到order.delay.queue【延时队列】
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange","order.create.order",null);
    }
    @Bean
    public Binding orderReleaseOrderBingding() {
        //，将路由键为order.release.order的消息路由到order.release.order.queue【消费队列】
        return new Binding("order.release.order.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange","order.release.order",null);
    }
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113170708467.png" alt="image-20240113170708467" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113170741573.png" alt="image-20240113170741573" style="zoom:67%;" />

###### 测试

- 监听消息的方法可以有三种参数（不分数量，顺序）Object content, Message message, Channel channel

- channel可以用来拒绝消息，否则自动ack

- 模拟下单成功发送消息

```java
@ResponseBody
@GetMapping("/test/createOrder")
public String createOrder() {
    OrderEntity orderEntity = new OrderEntity();//模拟下单成功
    orderEntity.setOrderSn(UUID.randomUUID().toString());
    orderEntity.setCreateTime(new Date());
    //给MQ发送消息
    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",orderEntity);
    return "ok";
}
```

- 接收消息

```java
@RabbitListener(queues = "order.release.order.queue")
public void listerner(OrderEntity entity, Channel channel, Message message) throws IOException {
    System.out.println(entity.toString());
    channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);//签收消息
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240113172801934.png" alt="image-20240113172801934" style="zoom:67%;" />.

#### 消息可靠性考虑的问题

##### 消息丢失

- 消息发送出去，由于网络问题没有抵达服务器

  - 做好容错方法（try-catch），发送消息可能会网络失败，**失败后要有重试机制**，可记录到数据库，采用定期扫描重发的方式
  - 做好日志记录，每个消息状态是否都被服务器收到都应该记录【可以在对应微服务下创建消息日志表，保存消息的详细信息】

  ```sql
  CREATE TABLE `mq_message` (
  	`message_id` char(32) NOT NULL, 
      `content` text, `to_exchane` varchar(255) DEFAULT NULL, 
      `routing_key` varchar(255) DEFAULT NULL, 
      `class_type` varchar(255) DEFAULT NULL, 
      `message_status` int(1) DEFAULT '0' COMMENT '0-新建 1-已发送 2-错误抵达 3-已抵达', 
      `create_time` datetime DEFAULT NULL, 
      `update_time` datetime DEFAULT NULL, 
      PRIMARY KEY (`message_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  ```

  - 做好定期重发，如果消息没有发送成功，定期去数据库扫描未成功的消息进行重发

- 消息抵达Broker，Broker要将消息写入磁盘（持久化）才算成功，此时Broker尚未持久化完成就宕机

  - publisher也必须加入**确认回调机制**，确认成功的消息，修改数据库消息状态

- 自动ACK的状态下，消费者收到消息，但没来得及处理消息然后宕机

- 一定开启手动ACK，消费成功才移除，失败或者没来得及处理就noAck并重新入队

- 总结防止消息丢失的解决方法

  - 做好两端的消息确认机制【pulisher（成功回调确认），consumer（手动ack）】
  - 每个发送的消息都在数据库做好记录，定期将失败的消息重发

##### 消息重复

- 消息消费成功，事务已经提交，ack时机器宕机，导致没有ack成功，Broker的消息重新由unack变为ready，并发送给其他消费者 

- 消息消费失败，由于重试机制，自动又将消息发送出去【业务允许的消息重复】

- 成功消费，ack时宕机，消息由unack变为ready，Broker又重新发送

- 解决

  - 消费者的业务消费接口应该设计为**幂等性**的。比如扣库存有工作单的状态标志
  - 使用**防重表**（redis/mysql），发送消息每一个都有业务的唯 一标识，处理过就不用处理
  - rabbitMQ的每一个消息都有redelivered字段，可以获取是否是被重新投递过来的，而不是第一次投递过来的

  ```java
  message.getMessageProperties().getRedelivered();
  ```

##### 消息积压

- 消费者宕机积压
- 消费者消费能力不足积压
- 发送者发送流量太大
- 解决
  - 发送端可以限制流量【后续会提到】
  - 消费端
    - 上线更多的消费者，进行正常消费
    - 上线专门的队列消费服务，将消息先批量取出来，记录数据库，离线慢慢处理

### 接口幂等性

#### 定义

- 接口幂等性就是用户**对于同一操作发起的一次请求或者多次请求的结果是<a>一致</a>的**，不会因为多次点击而产生了副作用
- 比如说支付场景，用户购买了商品支付扣款成功，但是返回结 果的时候网络异常，此时钱已经扣了，用户再次点击按钮，此时会进行第二次扣款，返回结果成功，用户查询余额返发现多扣钱了，流水记录也变成了两条．．．，这就没有保证接口的幂等性

#### 需要防止的场景

- 用户多次点击按钮 
- 用户页面回退再次提交 
- 微服务互相调用，由于网络问题，导致请求失败，feign触发重试机制 
- 其他业务情况

#### 幂等和非幂等举例 

- 以 SQL 为例，有些操作是天然幂等的
  - SELECT * FROM table WHER id=?，**查询**无论执行多少次都不会改变状态，是天然的幂等
  - UPDATE tab1 SET col1=1 WHERE col2=2，**固定值更新**无论执行成功多少次状态都是一致的，也是幂等操作
  - delete from user where userid=1，**指定记录**删除，执行多少次结果都一样，具备幂等性 
  - insert into user(userid,name) values(1,'a') ，假如**userid为唯一主键**，**插入时指定固定主键**，即重复操作上面的业务，只会插入一条用户数据，具备幂等性
- 不为幂等的情况
  - UPDATE tab1 SET **col1=col1+1** WHERE col2=2，每次执行的结果都会发生变化，不是幂等的
  - insert into user(userid,name) values(1,'a') ，**假如userid不是主键，且可以重复**，那上面业务多次操作，数据都会新增多条，不具备幂等性

#### 幂等解决方案

##### token机制

###### 步骤

1. 服务端提供了发送 token 的接口。在分析业务的时候，哪些业务是存在幂等问题的， 就必须在执行业务前，先去获取 token，服务器会把 token 保存到 redis 中
2. 然后调用业务接口请求时，把 token 携带过去，一般放在请求头部
3. 服务器判断 token 是否存在 redis 中，存在表示第一次请求，然后删除 token，继续执行业务
4. 如果判断 token 不存在 redis 中，就表示是重复操作，直接返回重复标记给 client，这样就**保证了业务代码<a>不被重复</a>执行**

###### 危险性

- 先删除token还是后删除token
  - 先删除可能导致，业务确实没有执行，**重试还带上之前token**，由于防重设计导致， 请求还是不能执行
  - 后删除可能导致，业务处理成功，但是服务闪断，出现超时，**没有删除token**，别人继续重试，**导致业务被执行两遍**
  - 最好设计为先删除 token，如果业务调用失败，就重新获取token再次请求
- Token获取、比较和删除必须是**原子性**
  - redis.get(token) 、token.equals、redis.del(token)如果这两个操作不是原子，高并发下可能会get到同样的数据，判断都成功，继续业务并发执行
  - 可以在redis**使用lua脚本**实现`if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end`

##### 各种锁机制

###### 数据库悲观锁 

- select * from xxxx where id = 1 for update
- 悲观锁使用时一般伴随事务一起使用，数据锁定时间可能会很长，需要根据实际情况选用
- 另外要注意的是，**id字段一定是主键或者唯一索引**，不然可能造成锁表的结果，处理起来会非常麻烦 

###### 数据库乐观锁 

- 这种方法适合在**更新**的场景中， 乐观锁主要使用于处理**读多写少**的问题
- update t_goods set count = count -1 , version = version + 1 where good_id=2 and version = 1 
- 根据version版本，也就是**在操作库存前先获取当前商品的version版本号**，然后操作的时候带上此 version 号
  - 第一次操作库存时，得到version为1，调用库存服务更新成功version就变成了2
  - 但返回给订单服务出现了问题，订单服务又一次发起调用库存服务，当订单服务传入的version还是1，再执行上面的sql 语句时，就不会执行
  - 因为version 已经变为2了，where条件就不成立，这样就保证了不管调用几次，只会真正的处理一次

###### 业务层分布式锁 

- 如果多个机器可能在同一时间同时处理相同的数据，比如多台机器定时任务都拿到了相同数据处理，就可以加分布式锁，锁定此数据，处理完成后释放锁，获取到锁的必须**先判断这个数据是否被处理过**

##### 各种唯一约束

###### 数据库唯一约束

- 插入数据，应该按照唯一索引进行插入，比如订单号，相同的订单就不可能有两条记录插入
- 在数据库层面防止重复。 这个机制是利用了数据库的主键唯一约束的特性，解决了在 insert 场景时幂等问题
- 但**主键的要求<a>不是自增</a>的主键**，这样就**需要业务生成全局唯一的主键**
- 如果是分库分表场景下，**路由规则要保证<a>相同请求</a>下落地在<a>同一个数据库和同一表</a>中**，要不然数据库主键约束就不起效果了，因为是不同的数据库和表主键不相关

###### redis的set防重 

- 很多数据需要处理，只能被处理一次
- 比如可以计算数据的MD5将其放入redis的set， 每次处理数据，先看这个MD5是否已经存在，存在就不处理

###### 防重表 

- 使用订单号orderNo做为去重表的唯一索引，把唯一索引插入去重表，再进行业务操作，且他们在同一个事务中
- 这个保证了重复请求时，因为去重表有唯一约束，导致请求失败，避免了幂等问题
- 需要注意的是，**去重表和业务表应该在同一库中**，这样就**保证了在同一个事务**，即使业务操作失败了，也会把去重表的数据回滚，这个很好的保证了数据一致性
- 之前说的redis防重也算

###### 全局请求唯一id 

- 调用接口时，生成一个唯一 id，redis将数据保存到集合中（去重），存在即处理过
- 可以使用nginx设置每一个请求的唯一 id `proxy_set_header X-Request-Id $request_id`

### 分布式事务

#### 事务基本概念

##### 事务的基本性质 

- 数据库事务的几个特性：原子性(Atomicity )、一致性( Consistency )、隔离性( Isolation) 和持久性(Durabilily)，简称就是 ACID

  - 原子性：一系列的操作整体不可拆分，要么同时成功，要么同时失败
  - 一致性：数据在事务的前后，业务整体一致【转账--A:1000；B:1000--》转 200事务成功;--》A：800 B：1200】
  - 隔离性：事务之间互相隔离
  - 持久性：一旦事务成功，数据一定会落盘在数据库

- 在以往的单体应用中，我们多个业务操作使用同一条连接操作不同的数据表，一旦有异常， 我们可以很容易的整体回滚； 

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240110164201728.png" alt="image-20240110164201728" style="zoom:67%;" />.

  - Business：具体的业务代码 
  - Storage：库存业务代码，扣库存 
  - Order：订单业务代码，保存订单 
  - Account：账号业务代码，减账户余额 
  - 比如买东西业务，扣库存，下订单，账户扣款，是一个整体，必须同时成功或者失败
  - 一个事务开始，代表以上的所有操作都在同一个连接里面

##### 事务的隔离级别 

- READ UNCOMMITTED（读未提交） 该隔离级别的事务会**读到其它未提交事务的数据**，此现象也称之为脏读
- READ COMMITTED（读已提交） 一个事务可以读取另一个已提交的事务，**多次读取会造成不一样的结果**【即在当前事务执行期间，另一个事务进行多次提交】，此现象称为**不可重复**读问题，Oracle 和 SQL Server的默认隔离级别
- REPEATABLE READ（可重复读） 该隔离级别是**MySQL默认的隔离级别**，在同一个事务里，select的结果是**事务开始时时间点的状态**
  - 同样的select操作读到的结果会是一致的，但是会有**幻读**现象
  - MySQL的InnoDB引擎可以通过next-key locks机制（参考下文"行锁的算法"一节）来避免幻读
- SERIALIZABLE（序列化） 在该隔离级别下事务都是**串行顺序**执行的，MySQL 数据库的InnoDB引擎会给读操作隐式加一把读共享锁，从而避免了脏读、不可重读复读和幻读问题
- @Transactional的isolation属性可以设置事务的隔离级别，越大的隔离级别并发性能更低

##### 事务的传播行为

- 事务传播行为指的就是当一个事务方法**被另一个事务方法调用时**，这个事务方法应该如何运行

  - 当两个方法共用一个事务时，被调用的事务方法设置的事务属性就失效了【**共用调用者的事务属性**】
  - **同一个类**下的方法互相调用，即使设置了不同的事务注解，依然以第一个执行的方法的事务为准
    - 原因是**事务是用代理对象控制的**
    - 解决方法
      1. 导入spring-boot-starter-aop
      2. @EnableTransactionManagement(proxyTargetClass = true)
      3. @EnableAspectJAutoProxy(exposeProxy=true)开启aspectj动态代理功能（即使没有接口也可以创建动态代理）
         - exposeProxy=true对外暴露代理对象
      4. 使用AopContext.currentProxy() 生成代理对象，通过代理对象调用其它方法

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240110171310757.png" alt="image-20240110171310757" style="zoom:50%;" />.

- PROPAGATION_**REQUIRED**：如果当前没有事务，就创建一个新事务，如果当前存在事务，就加入该事务，该设置是最常用的设置

- PROPAGATION_**SUPPORTS**：支持当前事务，如果当前存在事务，就加入该事务，如果当 前不存在事务，就以非事务执行

- PROPAGATION_**MANDATORY**：支持当前事务，如果当前存在事务，就加入该事务，如果 当前不存在事务，就抛出异常

- PROPAGATION_**REQUIRES_NEW**：创建新事务，**无论当前存不存在事务，都创建新事务**

- PROPAGATION_**NOT_SUPPORTED**：以非事务方式执行操作，如果当前存在事务，就把当 前事务挂起

- PROPAGATION_**NEVER**：以非事务方式执行，如果当前存在事务，则抛出异常

- PROPAGATION_**NESTED**：如果当前存在事务，则在嵌套事务内执行；如果当前没有事务，则执行与REQUIRED类似的操作

#### 分布式事务引入

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240110160125105.png" alt="image-20240110160125105" style="zoom:67%;" />.

- 本地事务在分布式系统中只能控制自己的事务回滚，**无法控制远程事务回滚**
  - 订单服务在库存锁定之前抛异常，库存锁定不运行，全部回滚， 撤销操作
  - 库存服务锁定失败全部回滚，抛出的异常让订单感受到，订单服务也继续回滚
  - 库存服务锁定成功了，但是网络原因返回数据途中出问题【**假失败**】，导致订单服务回滚
  - 库存服务锁定成功了，**库存服务下面的逻辑发生故障**，订单回滚了，但是远程服务无法回滚
    - 利用消息队列实现最终一致 库存服务锁定成功后发给消息队列消息（当前库 存工作单），过段时间自动解锁，解锁时先查询 订单的支付状态
    - 解锁成功修改库存工作单详情项状态为已解锁，库存服务锁定库存，订单服务，下订单用户服务扣减积分 
- 分布式事务需要解决的问题
  - 远程服务**假失败**： 假设库存服务其实成功了，由于**网络故障**等没有返回，导致订单回滚，库存却扣减
  - 远程服务执行完成之后下面的其他方法出现问题 ，导致**已执行的<a>远程请求</a>无法回滚**

#### 分布式事务概念

##### CAP定理

- CAP原则又称CAP 定理，指的是在一个分布式系统中
  - 一致性（Consistency）：在分布式系统中的**所有数据备份<a>在同一时刻</a>是否同样的值**（所有节点访问同一份最新的数据副本）
  - 可用性（Availability）：在集群中**<a>一部分节点故障后</a>，集群整体是否还能响应客户端的读写请求**（对数据更新具备高可用性）
  - 分区容错性（Partition tolerance）：大多数分布式系统都分布在多个子网络，每个子网络就叫做一个区（partition）， 分区容错的意思是，**区间通信可能失败**。比如，一台服务器放在中国，另一台服务器放在美国，这就是两个区，它们之间可能无法通信
- CAP 原则指的是，这三个要素**最多只能同时实现两点**，不可能三者兼顾
- 一般来说，分区容错无法避免，因此可以认为CAP的**P总是成立**，剩下的 C 和 A 无法同时做
- 对于多数大型互联网应用的场景，主机众多、部署分散，而且现在的集群规模越来越大，所以节点故障、网络故障是常态，而且要保证服务可用性达到 99.99999%，即保证 P 和 A，舍弃C

##### 实现一致性的算法

- 分布式系统中**实现一致性**有raft、paxos算法 

- http://thesecretlivesofdata.com/raft/ 【raft解释】

  - 集群中的节点有三种状态，leader、follower、candidate，假设节点都为follower状态，此时没有leader，那达到超时时间就会有节点变成候选人，通过选举产生leader，此时leader和客户端通信【leader选举】

  - 当客户端发送命令时，leader先将该命令记录在日志中，然后通知其它follower节点，当其它所有节点收到时且返回给leader之后，leader才执行命令，并且通知其它follower可以执行命令了【日志复制】

  - leader选举过程中有两个超时时间

    - 选举超时时间，**follower变成一个候选人**需要等待的时间【一般是150-300ms，又称节点自旋时间】，候选人会向其它节点发起投票通知【唱票】，其它节点**投出票时会刷新自己的自旋时间**【每个节点**每轮只有一次投票**，候选人会自投】
    - 心跳超时时间，leader每隔一段时间就要**向其它随从发送心跳**来维护心跳连接，**follower收到消息会刷新自旋时间**【心跳超时时间一定比节点自旋时间短】，当有一个新的随从变成候选者时【当前leader挂了等等】，停止发送心跳
    - 同票的情况会进行下一轮选举【follower收到一个候选人的通知就不会接收其它候选人的请求了】

  - 日志复制

    - 日志是在每一个心跳中发送出去的，在收到请求的下一个心跳携带日志数据
    - 大多数节点回复了（n/2+1），leader就会执行命令
    - 如果产生网络分区，不同分区有各自的领导
      - 下图两个分区中，上分区的领导可以收到大多数节点的恢复，所以会执行命令，下分区只写入日志不执行
      - 当恢复网络时，**低轮次的领导会退位**，所有跟老领导相关的操作都要回滚，然后和新领导同步

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240110184848915.png" alt="image-20240110184848915" style="zoom:50%;" />==》<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240110184916725.png" alt="image-20240110184916725" style="zoom:50%;" />

##### BASE理论

- 对CAP理论的延伸，思想是即使无法做到强一致性（CAP 的一致性就是强一致性），但可以采用适当的**采取弱一致性**，即**最终一致性**
- BASE 是指基本可用（Basically Available）
  - 基本可用是指分布式系统在出现故障的时候，允许损失部分可用性（例如响应时间、 功能上的可用性），允许**损失<a>部分</a>可用性**
    - 响应时间上的损失：正常情况下搜索引擎需要在 0.5 秒之内返回给用户相应的查询结果，但由于出现故障（比如系统部分机房发生断电或断网故障），查询 结果的响应时间增加到了1~2秒
    - 功能上的损失：购物网站在购物高峰（如双十一）时，为了保护系统的稳定性， 部分消费者可能会被引导到一个降级页面
    - 需要注意的是，基本可用绝不等价于系统不可用 
  - 软状态（ Soft State）是指**允许系统存在中间状态**，而该中间状态**不会影响系统整体可用性**
    - 分布式存储中一般一份数据会有**多个副本**，允许不同副本同步的延时就是软状态的体现
    - mysql replication的异步复制也是一种体现
  - 最终一致性（ Eventual Consistency）是指系统中的所有数据副本**经过一定时间后，最终能够达到一致的状态**
- 强一致性、弱一致性、最终一致性
  - 对于关系型数据库，要求更新过的数据能被后续的访问都能看到，这是强一致性
  - 如果能容忍后续的部分或者全部访问不到，则是弱一致性
  - 如果经过一段时间后要求能访问到更新后的数据，则是最终一致性
  - 弱一致性和强一致性相反，**最终一致性是弱一致性的一种特殊情况**

#### 分布式事务几种方案

##### 刚性事务-2PC模式 

- 数据库支持的 2PC【2 phase commit 二阶提交】，又叫做 XA Transactions
- MySQL 从 5.5 版本开始支持，SQL Server 2005 开始支持，Oracle 7 开始支持
- XA是一个两阶段提交协议，该协议分为以下两个阶段
  - 第一阶段：事务协调器要求每个涉及到事务的数据库**预提交**(precommit)此操作，并**反映是否可以提交**
  - 第二阶段：事务协调器**要求每个数据库提交数据**
  - 如果有**任何一个数据库<a>否决</a>此次提**交，那么**所有数据库都会被要求回滚**它们在此事务中的那部分信息

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240110191448945.png" alt="image-20240110191448945" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240110191502392.png" alt="image-20240110191502392" style="zoom:67%;" />

- XA 协议比较简单，而且一旦商业数据库实现了 XA 协议，使用分布式事务的成本也比较低
- **XA性能不理想**，特别是在交易下单链路，往往并发量很高，XA 无法满足高并发场景
- XA 目前在商业数据库支持的比较理想，在 mysql 数据库中支持的不太理想，mysql 的 XA 实现，没有记录 prepare 阶段日志，主备切换回导致主库与备库数据不一致
- 许多 nosql 也没有支持 XA，这让 XA 的应用场景变得非常狭隘
- 也有 3PC，引入了超时机制（无论协调者还是参与者，在向对方发送请求后，若长时间未收到回应则做出相应处理）

##### 柔性事务

- 刚性事务：遵循 ACID 原则，强一致性
- 柔性事务：遵循 BASE 理论，最终一致性, 与刚性事务不同，柔性事务允许一定时间内，不同节点的数据不一致，但要求最终一致

###### TCC事务补偿型

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240110192511960.png" alt="image-20240110192511960" style="zoom:67%;" />.

-  TCC 模式，是指支持把自定义的分支事务纳入到全局事务的管理中
   1. 一阶段 prepare 行为：调用自定义的 prepare 逻辑【预先准备】
   2. 二阶段 commit 行为：调用自定义的 commit 逻辑【通知提交】
   3. 三阶段 rollback 行为：调用自定义的 rollback 逻辑【最关键，任何一个事务失败就全部回滚或者进行补偿】

###### 最大努力通知型

- 按规律进行通知，不保证数据一定能通知成功，但会提供可查询操作接口进行核对
- 这种方案主要用在与第三方系统通讯时，比如调用微信或支付宝支付后的支付结果通知
- 这种方案也是结合 MQ 进行实现，例如通过 MQ 发送 http 请求，设置最大通知次数，达到通知次数后即不再通知
- 案例：银行通知、商户通知等（各大交易业务平台间的商户通知：多次通知、查询校对、对 账文件），支付宝的支付成功异步回调

###### 可靠消息+最终一致性

- 异步确保型
- 业务处理服务在业务事务提交之前，向实时消息服务请求发送消息，实时消息服务只记录消息数据，而不是真正的发送
- 业务处理服务在业务事务提交之后，向实时消息服务确认发送
- 只有在得到确认发送指令后，实时消息服务才会真正发送
- 防止消息丢失
  - 做好消息确认机制（pulisher，consumer【手动 ack】）
  - 每一个发送的消息都在数据库做好记录，定期将失败的消息再次发送一 遍

```sql
CREATE TABLE `mq_message` (
	`message_id` char(32) NOT NULL, 
    `content` text, `to_exchane` varchar(255) DEFAULT NULL, 
    `routing_key` varchar(255) DEFAULT NULL, 
    `class_type` varchar(255) DEFAULT NULL, 
    `message_status` int(1) DEFAULT '0' COMMENT '0-新建 1-已发送 2-错误抵达 3-已抵达', 
    `create_time` datetime DEFAULT NULL, 
    `update_time` datetime DEFAULT NULL, 
    PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
```

### Seata

#### 基础知识

##### 简介

- Seata 是一款开源的分布式事务解决方案，致力于在微服务架构下提供高性能和简单易用的分布式事务服务
- 官方文档：https://seata.io/zh-cn/

##### 核心概念

- TC - 事务协调者：**维护<a>全局和分支</a>事务的状态**，驱动全局事务提交或回滚
- TM - 事务管理器：**定义<a>全局</a>事务的范围**，开始全局事务、提交或回滚全局事务
- RM - 资源管理器：**管理<a>分支</a>事务处理的资源**，与TC交谈以注册分支事务和报告分支事务的状态，并驱动分支事务提交或回滚

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240112141217536.png" alt="image-20240112141217536" style="zoom: 50%;" />.

#### 快速入门

1. 为需要开启分布式事务的微服务都创建回滚日志表【SEATA AT 模式需要 `UNDO_LOG` 表】

   - 事务开始会先保存当前记录的状态，回滚就恢复到该状态

   ```sql
   -- 注意此处0.3.0+ 增加唯一索引 ux_undo_log
   CREATE TABLE `undo_log` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT,
     `branch_id` bigint(20) NOT NULL,
     `xid` varchar(100) NOT NULL,
     `context` varchar(128) NOT NULL,
     `rollback_info` longblob NOT NULL,
     `log_status` int(11) NOT NULL,
     `log_created` datetime NOT NULL,
     `log_modified` datetime NOT NULL,
     `ext` varchar(100) DEFAULT NULL,
     PRIMARY KEY (`id`),
     UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
   ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
   ```

2. 安装事务协调器seata-server

3. 导入依赖，alibaba的seata场景驱动器会内嵌一个seata-server，版本与spring cloud版本有关，但是seata-server必须要和自己安装的服务器版本一致，所以需要排除并且自行导入，[版本说明 · alibaba/spring-cloud-alibaba Wiki · GitHub](https://github.com/alibaba/spring-cloud-alibaba/wiki/版本说明)

   ```xml
   <dependency>
       <groupId>io.seata</groupId>
       <artifactId>seata-spring-boot-starter</artifactId>
     	<version>2.0.0</version>
   </dependency>
   <dependency>
       <groupId>com.alibaba.cloud</groupId>
       <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
       <exclusions>
           <exclusion>
               <groupId>io.seata</groupId>
               <artifactId>seata-spring-boot-starter</artifactId>
           </exclusion>
       </exclusions>
   </dependency>
   ```

4. 所有需要用到分布式事务的微服务需要修改nacos的分组，要和seata-server的一致

   - 如果设置一致，网关的分组为DEFAULT_GROUP，这样子网关就识别不到SEATA_GROUP下的微服务了【会报java.net.UnknownHostException异常】，所以还是**统一设置成DEFAULT_GROUP**

   ```yaml
   cloud:
     nacos:
       discovery:
         server-addr: 127.0.0.1:8848
         group: SEATA_GROUP
   ```

5. @GlobalTransactional：标注在总事务中，其他分支事务，继续使用@Transactional即可

6. **老版本**的分支事务都需要使用seata DataSourceProxy代理自己的数据源，否则事务无法回滚【**可以忽略**】

   ```java
   @Configuration
   public class DataSourceConfig {
       @Bean
       @ConfigurationProperties(prefix = "spring.datasource")
       public DruidDataSource druidDataSource() {
       	return new DruidDataSource();//先注入原本配置的数据源
       }
       /**
       * 需要将 DataSourceProxy 设置为主数据源，否则事务无法回滚
       *
       * @param druidDataSource The DruidDataSource * @return The default datasource */
       @Primary
       @Bean("dataSource")
       public DataSource dataSource(DruidDataSource druidDataSource) {
       	return new DataSourceProxy(druidDataSource);//代理原本配置的数据源
       }
   }
   ```

#### seata-serveryaml配置

- 视频来源**动力节点**

##### 存储模式

- store用于配置存储模式，SeataServer 需要对全局事务与分支事务进行存储，以便对它们进行管理，mode配置存储模式的类型

  ```yaml
  seata:
      store:
       mode: db
       session:
         mode: db
       lock:
         mode: db
  ```

- seata-server存储模式目前支持三种，不同模式的具体配置如下【配置和mode是同级的】

  - file模式：将相关数据存储在本地文件中，一般用于Seata Server的单机测试

    ```yaml
    file:
      dir: sessionStore
      max-branch-session-size: 16384
      max-global-session-size: 512
      file-write-buffer-cache-size: 16384
      session-reload-read-size: 100
      flush-disk-mode: async
    ```

  - db模式：存储在数据库中，一般用于生产环境下的Seata Server集群部署，生产环境下**使用最多的模式**

    - 该模式相关的建表语句在安装目录的\script\server下【seata数据库需要自己创建】

    ```yaml
    db:
      datasource: druid
      db-type: mysql
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://192.168.32.100:3306/seata?rewriteBatchedStatements=true&useSSL=false
      user: root
      password: 1212go12
      min-conn: 10
      max-conn: 100
      global-table: global_table
      branch-table: branch_table
      lock-table: lock_table
      distributed-lock-table: distributed_lock
      query-limit: 1000
      max-wait: 5000
    ```

  - redis模式：存储在redis 中，一般用于生产环境下的Seata Server集群部署，**性能略高于db模式**

    ```yaml
    redis:
      mode: single
      # support: lua 、 pipeline
      type: lua
      database: 0
      min-conn: 10
      max-conn: 100
      password:
      max-total: 100
      query-limit: 1000
      single:
        host: 127.0.0.1
        port: 6379
      sentinel:
        master-name:
        sentinel-hosts:
        sentinel-password:
    ```

##### 注册中心和配置中心

- 配置nacos作为配置中心，seataServer.properties需要在nacos中创建发布并且指定SEATA_GROUP分组
  - 配置文件内容在安装目录的script\config-center\config.txt下，下面有seataServer.properties的详解

```yaml
seata:
  config:
    # support: nacos 、 consul 、 apollo 、 zk  、 etcd3
    type: nacos #
    nacos:
      server-addr: 127.0.0.1:8848
      # 如果在nacos上添加了命名空间，则配置命令空间ID
      namespace: 
      # 配置分组
      group: SEATA_GROUP
      context-path:
      ##if use MSE Nacos with auth, mutex with username/password attribute
      #access-key:
      #secret-key:
      data-id: seataServer.properties
```

- 配置nacos作为注册中心
  - 之前seata启动成功但是nacos一直识别不到是因为没有创建对应的命名空间，如果不指定就会放到默认的public空间中

```yaml
seata:
 registry:
    # support: nacos 、 eureka 、 redis 、 zk  、 consul 、 etcd3 、 sofa
    type: nacos
    preferred-networks: 30.240.*
    nacos:
      application: seata-server
      server-addr: 127.0.0.1:8848
      group: SEATA_GROUP
      namespace:
      cluster: default
      context-path:
      ##if use MSE Nacos with auth, mutex with username/password attribute
      #access-key:
      #secret-key:
```

##### seataServer.properties

- 该文件放在安装目录下的script\config-center\config.txt

- seataServer.properties的`service.vgroupMapping.default_tx_group=default`和`service.default`以及seata配置文件的`cluster: default`三者是相互关联的，如果已经有注册中心了可以忽略这些配置
- 存储模式需要和seata配置的一致，如果指定了某个存储模式，需要把该文件中另外两个模式的配置移除

```properties
#For details about configuration items, see https://seata.io/zh-cn/docs/user/configurations.html
#Transport configuration, for client and server
transport.type=TCP
transport.server=NIO
transport.heartbeat=true
transport.enableTmClientBatchSendRequest=false
transport.enableRmClientBatchSendRequest=true
transport.enableTcServerBatchSendResponse=false
transport.rpcRmRequestTimeout=30000
transport.rpcTmRequestTimeout=30000
transport.rpcTcRequestTimeout=30000
transport.threadFactory.bossThreadPrefix=NettyBoss
transport.threadFactory.workerThreadPrefix=NettyServerNIOWorker
transport.threadFactory.serverExecutorThreadPrefix=NettyServerBizHandler
transport.threadFactory.shareBossWorker=false
transport.threadFactory.clientSelectorThreadPrefix=NettyClientSelector
transport.threadFactory.clientSelectorThreadSize=1
transport.threadFactory.clientWorkerThreadPrefix=NettyClientWorkerThread
transport.threadFactory.bossThreadSize=1
transport.threadFactory.workerThreadSize=default
transport.shutdown.wait=3
transport.serialization=seata
transport.compressor=none

#Transaction routing rules configuration, only for the client
service.vgroupMapping.default_tx_group=default
#If you use a registry, you can ignore it
service.default.grouplist=127.0.0.1:8091
service.enableDegrade=false
service.disableGlobalTransaction=false

client.metadataMaxAgeMs=30000
#Transaction rule configuration, only for the client
client.rm.asyncCommitBufferLimit=10000
client.rm.lock.retryInterval=10
client.rm.lock.retryTimes=30
client.rm.lock.retryPolicyBranchRollbackOnConflict=true
client.rm.reportRetryCount=5
client.rm.tableMetaCheckEnable=true
client.rm.tableMetaCheckerInterval=60000
client.rm.sqlParserType=druid
client.rm.reportSuccessEnable=false
client.rm.sagaBranchRegisterEnable=false
client.rm.sagaJsonParser=fastjson
client.rm.tccActionInterceptorOrder=-2147482648
client.rm.sqlParserType=druid
client.tm.commitRetryCount=5
client.tm.rollbackRetryCount=5
client.tm.defaultGlobalTransactionTimeout=60000
client.tm.degradeCheck=false
client.tm.degradeCheckAllowTimes=10
client.tm.degradeCheckPeriod=2000
client.tm.interceptorOrder=-2147482648
client.undo.dataValidation=true
client.undo.logSerialization=jackson
client.undo.onlyCareUpdateColumns=true
server.undo.logSaveDays=7
server.undo.logDeletePeriod=86400000
client.undo.logTable=undo_log
client.undo.compress.enable=true
client.undo.compress.type=zip
client.undo.compress.threshold=64k
#For TCC transaction mode
tcc.fence.logTableName=tcc_fence_log
tcc.fence.cleanPeriod=1h
# You can choose from the following options: fastjson, jackson, gson
tcc.contextJsonParserType=fastjson

#Log rule configuration, for client and server
log.exceptionRate=100

#Transaction storage configuration, only for the server. The file, db, and redis configuration values are optional.
store.mode=db
store.lock.mode=db
store.session.mode=db
#Used for password encryption
#store.publicKey=

#These configurations are required if the `store mode` is `db`. If `store.mode,store.lock.mode,store.session.mode` are not equal to `db`, you can remove the configuration block.
store.db.datasource=druid
store.db.dbType=mysql
  driver-class-name: 
store.db.driverClassName=com.mysql.cj.jdbc.Driver
store.db.url=jdbc:mysql://192.168.32.100:3306/seata?rewriteBatchedStatements=true&useSSL=false
store.db.user=root
store.db.password=1212go12
store.db.minConn=5
store.db.maxConn=30
store.db.globalTable=global_table
store.db.branchTable=branch_table
store.db.distributedLockTable=distributed_lock
store.db.queryLimit=100
store.db.lockTable=lock_table
store.db.maxWait=5000

#Transaction rule configuration, only for the server
server.recovery.committingRetryPeriod=1000
server.recovery.asynCommittingRetryPeriod=1000
server.recovery.rollbackingRetryPeriod=1000
server.recovery.timeoutRetryPeriod=1000
server.maxCommitRetryTimeout=-1
server.maxRollbackRetryTimeout=-1
server.rollbackRetryTimeoutUnlockEnable=false
server.distributedLockExpireTime=10000
server.session.branchAsyncQueueSize=5000
server.session.enableBranchAsyncRemove=false
server.enableParallelRequestHandle=true
server.enableParallelHandleBranch=false

server.raft.cluster=127.0.0.1:7091,127.0.0.1:7092,127.0.0.1:7093
server.raft.snapshotInterval=600
server.raft.applyBatch=32
server.raft.maxAppendBufferSize=262144
server.raft.maxReplicatorInflightMsgs=256
server.raft.disruptorBufferSize=16384
server.raft.electionTimeoutMs=2000
server.raft.reporterEnabled=false
server.raft.reporterInitialDelay=60
server.raft.serialization=jackson
server.raft.compressor=none
server.raft.sync=true

#Metrics configuration, only for the server
metrics.enabled=false
metrics.registryType=compact
metrics.exporterList=prometheus
metrics.exporterPrometheusPort=9898
```

### Sentinel

#### 熔断降级限流 

##### 熔断 

- A 服务调用B服务的某个功能，由于网络不稳定问题或者B服务卡机，导致功能时间超长。如果这样子的次数太多，就可以直接**将B断路**（A 不再请求B接口），凡是调用B的直接**返回降级数据**，不必等待B的超长执行
- 这样B的故障问题，就不会级联影响到 A

##### 降级 

- 整个网站处于流量高峰期，服务器压力剧增，根据当前业务情况及流量，对一些服务和页面进行有策略的降级【**停止服务**，所有的调用直接返回降级数据】
- 以此缓解服务器资源的的压力，以保证核心业务的正常运行，同时也保持了客户和大部分客户的得到正确的相应

##### 熔断和降级异同

- 相同点
  - 为了保证集群大部分服务的可用性和可靠性，防止崩溃，牺牲小我
  - 用户最终都是体验到某个功能不可用 
- 不同点
  - 熔断是**被调用方故障**，触发的系统主动规则
  - 降级是基于全局考虑，人工停止一些异常服务，释放资源

##### 限流

- 对打入服务的请求流量进行控制，使服务能够承担不超过自己能力的流量压力

#### Sentinel简介

- 官方文档：https://github.com/alibaba/Sentinel/wiki/%E4%BB%8B%E7%BB%8D 
- 项目地址：https://github.com/alibaba/Sentinel
- Sentinel 以流量为切入点， 从流量控制、熔断降级、系统负载保护等多个维度保护服务的稳定性

##### Sentinel的特征

- 丰富的应用场景：Sentinel 承接了阿里巴巴近10 年的双十一大促流量的核心场景，例如秒杀（即突发流量控制在系统容量可以承受的范围）、消息削峰填谷、集群流量控制、实时熔断下游不可用应用等
- 完备的实时监控：Sentinel 同时提供实时的监控功能。您可以在控制台中看到接入 应用的单台机器秒级数据，甚至 500 台以下规模的集群的汇总运行情况
- 广泛的开源生态：Sentinel 提供开箱即用的与其它开源框架/库的整合模块，例如 与 Spring Cloud、Dubbo、gRPC 的整合。您只需要引入相应的依赖并进行简单的配 置即可快速地接入 Sentinel
- 完善的 SPI 扩展点：Sentinel 提供简单易用、完善的 SPI 扩展接口。您可以通过 实现扩展接口来快速地定制逻辑。例如定制规则管理、适配动态数据源等

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240126154837849.png" alt="image-20240126154837849" style="zoom: 80%;" />.

##### Sentinel分为两个部分

- 核心库（Java 客户端）不依赖任何框架/库，能够运行于所有 Java 运行时环境，同时对Dubbo / Spring Cloud 等框架也有较好的支持
- 控制台（Dashboard）基于 Spring Boot 开发，打包后可以直接运行，不需要额外的 Tomcat 等应用容器

##### Sentinel 基本概念 

- 资源
  - 是 Sentinel 的关键概念，它可以是Java应用程序中的任何内容
  - 例如由应用程序提供的服务，或由应用程序调用的其它应用提供的服务，甚至可以是一段代码
  - 在接下来的文档中，都会用资源来描述代码块。 只要通过Sentinel API 定义的代码，就是资源，能够被 Sentinel 保护起来
  - 大部分情况下， 可以使用方法签名，URL，甚至服务名称作为资源名来标示资源
- 规则：围绕资源的实时状态设定的规则，可以包括流量控制规则、熔断降级规则以及系统保护规则，所有规则可以动态实时调整

##### Hystrix与Sentinel比较

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240125232423336.png" alt="image-20240125232423336" style="zoom: 80%;" />.

#### 环境搭建

##### 控制台环境搭建

1. 导入依赖

   ```xml
   <dependency>
       <groupId>com.alibaba.cloud</groupId>
       <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
   </dependency>
   ```

2. 下载sentinel控制台jar包【版本要和依赖版本一致】

3. 在jar包所在位置进入命令行，启动控制台`java -jar sentinel-dashboard-1.8.6.jar`，访问sentinel控制台，账密都为sentinel

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240126163550388.png" alt="image-20240126163550388" style="zoom: 50%;" />.

4. 导入控制台依赖的微服务需要配置控制台信息

   ```properties
   spring.cloud.sentinel.transport.dashboard=127.0.0.1:8080 #控制台所在端口
   spring.cloud.sentinel.transport.port=8719 #本服务和控制台通信的端口
   ```

5. 启动项目，sentinel是懒启动，当监听的服务收到请求时才会显示规则信息

   - 所有规则**默认存储在服务的内存**中，服务关闭规则就会消失
   - 规则可以在对应的请求中点击按钮设置

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240126170103045.png" alt="image-20240126170103045" style="zoom:67%;" />.

   - 在新增规则中，自行给请求绑定规则

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240126170314625.png" alt="image-20240126170314625" style="zoom:67%;" />.

##### 开启可视化实时监控

1. 每个微服务导入审计功能，从而可以被实时监控

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

2. 然后配置配置需要暴露/监控的资源

```properties
management.endpoints.web.exposure.include=* #配置暴露所有
```

![image-20240127141050464](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127141050464.png)

##### 自定义流控响应

- 配置web回调管理器【spring5以上的版本需要实现BlockExceptionHandler接口重写handle方法】

```java
@Configuration
public class SentinelConfig implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
        R error = R.error(BizCodeEnume.TO_MANY_REQUEST.getCode(), BizCodeEnume.TO_MANY_REQUEST.getMsg());
        httpServletResponse.setCharacterEncoding("UTF-8");//设置响应编码
        httpServletResponse.setContentType("application/json");//设置响应数据类型
        httpServletResponse.getWriter().write(JSON.toJSONString(error));
    }
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127145128087.png" alt="image-20240127145128087" style="zoom:67%;" />.

#### 流量控制

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127155513135.png" alt="image-20240127155513135" style="zoom: 50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127155859596.png" alt="image-20240127155859596" style="zoom:50%;" />

- QPS：每秒请求数

- 集群阈值模式

  - 单机均摊：每台机器都不能超过单体均摊阈值
  - 总体阈值：集群中所有集群**总请求**不超过集群阈值

- 流控模式

  - 直接：就是直接限制受控请求
  - 关联：需要指定关联资源，当关联资源流量大的时候就对受控资源进行限制
  - 链路：需要指定入口资源，流控只对**从入口资源**一连串调用到受控请求的路径生效

- 流控效果

  - 直接拒绝/快速失败

  - Warm up(预热)：需要指定预热时间，一点一点放入流量，达到预热时长才会放完阈值请求【不会让峰值流量全都涌进来】

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127160519383.png" alt="image-20240127160519383" style="zoom:50%;" />.

  - 排队等待：阈值外的请求就排队等待，可以设置超时时间，只要等待的请求超时了也直接失败

#### 熔断降级

##### 设计理念 

- Sentinel 和 Hystrix 的原则是一致的：当检测到调用链路中某个资源出现不稳定的表现，例如请求响应时间长或异常比例升高的时候，则对这个资源的调用进行限制，让请求快速失败， 避免影响到其它的资源而导致级联故障

- 在限制的手段上，Sentinel 和 Hystrix 采取了完全不一样的方法
  - Hystrix 通过线程池隔离的方式来对依赖（在 Sentinel 的概念中对应 资源）进行了隔离
    - 好处是资源和资源之间做到了最彻底的隔离
    - 缺点是除了增加了线程切换的成本（过多的线程池导致线程数目过多），还需要预先给各个资源做线程池大小的分配 
  - Sentinel 对这个问题采取了两种手段
    - 通过并发线程数进行限制 
      - 和资源池隔离的方法不同，Sentinel 通过限制资源并发线程的数量，来减少不稳定资源对其它资源的影响
      - 这样不但没有线程切换的损耗，也不需要预先分配线程池的大小
      - 当某个资源出现不稳定的情况下，例如响应时间变长，对资源的直接影响就是会造成线程数的逐步堆积，当线程数在特定资源上堆积到一定的数量之后，对该资源的新请求就会被拒绝，堆积的线程完成任务后才开始继续接收请求
    - 通过响应时间对资源进行降级 
      - 除了对并发线程数进行控制以外，Sentinel 还可以通过响应时间来快速降级不稳定的资源
      - 当依赖的资源出现响应时间过长后，所有对该资源的访问都会被直接拒绝，直到过了指定的时间，窗口之后才重新恢复

##### 调用方的熔断保护

1. 开启sentinel远程调用监控功能`feign.sentinel.enabled=true`

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127162149580.png" alt="image-20240127162149580" style="zoom:67%;" />.

2. 在fegin远程调用接口中配置出错时调用的接口

   ```java
   @FeignClient(value = "gulimall-seckill",fallback = SeckillFeginFailback.class)
   //SeckillFeginFailback秒杀服务的失败回调类
   @Slf4j
   @Component
   public class SeckillFeginFailback implements SeckillFeginService {
       @Override
       public R getSkuSeckillInfo(Long skuId) {
           log.error("秒杀服务---远程调用失败");
           return R.error(BizCodeEnume.TO_MANY_REQUEST.getCode(), BizCodeEnume.TO_MANY_REQUEST.getMsg());
       }
   }
   ```

3. 模拟秒杀服务宕机，页面还可以正常访问

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127162832861.png" alt="image-20240127162832861" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127162845610.png" alt="image-20240127162845610" style="zoom: 33%;" />

##### 手动指定降级策略

###### 降级策略

- 一旦触发降级，就默认调用远程服务的熔断回调方法

+ 慢调用比例 (`SLOW_REQUEST_RATIO`)
  + 选择以慢调用比例作为阈值，需要设置允许的慢调用 RT（即最大的响应时间），请求的响应时间大于该值则统计为慢调用
  + 当单位统计时长（`statIntervalMs`）内请求数目大于设置的最小请求数目，并且慢调用的比例大于阈值，则接下来的熔断时长内请求会自动被熔断
  + 经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求响应时间小于设置的慢调用RT则结束熔断，若大于设置的慢调用RT则会再次被熔断
+ 异常比例 (`ERROR_RATIO`)
  + 当单位统计时长（`statIntervalMs`）内请求数目大于设置的最小请求数目，并且异常的比例大于阈值，则接下来的熔断时长内请求会自动被熔断
  + 经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求成功完成（没有错误）则结束熔断，否则会再次被熔断。异常比率的阈值范围是 `[0.0, 1.0]`，代表 0% - 100%
+ 异常数 (`ERROR_COUNT`)
  + 当单位统计时长内的异常数目超过阈值之后会自动进行熔断
  + 经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求成功完成（没有错误）则结束熔断，否则会再次被熔断

##### 远程服务提供方降级

- 超大流量的时候，必须牺牲一些远程服务
- 尽管提供方【远程服务】被降级，调用方还是会调用该远程服务，被降级提供方依旧是在运行的，但是不运行自己的业务逻辑，默认**返回降级后的数据**

#### 自定义受保护资源

##### 抛出异常的方式

```java
try(Entry entry= SphU.entry("资源名")) {
	...
}catch (BlockException e){
	log.error("资源被限流，{}",e.getMessage());
}
```

- 可以单独给自定义资源设置规则

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127190443055.png" alt="image-20240127190443055" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127190811881.png" alt="image-20240127190811881" style="zoom:67%;" />

##### 基于注解

- `@SentinelResource("资源名")`标识在需要受保护的方法上

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127191104017.png" alt="image-20240127191104017" style="zoom:67%;" />.

- 受控会抛出异常

  ```java
  Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is java.lang.reflect.UndeclaredThrowableException] with root cause
  com.alibaba.csp.sentinel.slots.block.flow.FlowException: null
  ```

- 可以设置blockHandler/fallback属性来指定限流/降级之后调用的方法

  - blockHandler是针对资源的回调
    - 除了方法名之外，其余的**方法签名信息需要和限流方法的一致**，而且一般要**和原方法在同一个类**
    - 返回值类型要和方法的参数列表需要和原方法一致，但是**可以接收异常信息**

  ```java
  @SentinelResource(value = "getSkuSeckillInfoResource",blockHandler = "blockHandler" )
  public SeckillSkuRelationEntity getSkuSeckillInfo(Long skuId) {
  	...
  }    
  public SeckillSkuRelationEntity blockHandler(Long skuId,BlockException e){
      log.error("getSkuSeckillInfo方法被限流了{}",e.getMessage());
      return null;
  }
  ```

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127193538552.png" alt="image-20240127193538552" style="zoom:50%;" />.

  - fallback是针对所有类型异常的处理
    - 方法签名要求和blockHandler是一样的，可以额外接收异常信息
    - 处理的方法可以写在别的类中【用fallbackClass指定所在类，并且别的类处理方法需要指定为静态方法】

#### 网关流控

##### 环境搭建

1. 导入依赖

   ```xml
   <dependency>
       <groupId>com.alibaba.cloud</groupId>
       <artifactId>spring-cloud-alibaba-sentinel-gateway</artifactId>
       <version>2021.0.5.0</version>
   </dependency>
   ```

2. 默认监听网关中配置的路由id

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127195354691.png" alt="image-20240127195354691" style="zoom:67%;" />.

3. 流控规则新增属性

   - 间隔：每隔一段时间进行统计，即一段时间间隔内不能超过指定的阈值
   - Burst size：额外允许的请求数目

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127195553508.png" alt="image-20240127195553508" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127195830025.png" alt="image-20240127195830025" style="zoom:67%;" />

   * 还可以根据请求属性进行控制

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127200239722.png" alt="image-20240127200239722" style="zoom:67%;" />.

4. 可以自定义控制的api分组，

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127201441042.png" alt="image-20240127201441042" style="zoom: 67%;" />![image-20240127201544848](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127201544848.png)

##### 自定义流控返回

- 这一块涉及spring5的响应式编程的内容

```java
@Configuration
public class SentinelConfig {
    public SentinelConfig(){
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            //Mono Flux都是响应式编程的特性，参照spring5
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                R error = R.error(BizCodeEnume.TO_MANY_REQUEST.getCode(), BizCodeEnume.TO_MANY_REQUEST.getMsg());
                String jsonString = JSON.toJSONString(error);
                Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(jsonString), String.class);
                return body;
            }
        });
    }
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240127203126120.png" alt="image-20240127203126120" style="zoom:67%;" />.

### Sleuth+Zipkin服务链路追踪

#### 为什么用 

- 一个分布式系统往往有很多个服务单元，由于服务单元数量众多，业务的复杂性，如果出现了错误和异常，很难去定位
- 一个请求可能需要调用很多个服务，而内部服务的调用复杂性，决定了问题难以定位
- 所以必须实现分布式链路追踪，去跟进一个请求到底有哪些服务参与， 参与的顺序又是怎样的，从而达到每个请求的步骤清晰可见，出了问题，很快定位
- 链路追踪组件有 Google 的 Dapper，Twitter的Zipkin，以及阿里的Eagleeye （鹰眼）等，它们都是非常优秀的链路追踪开源组件

#### 基本术语

- 官方文档：https://cloud.spring.io/spring-cloud-static/spring-cloud-sleuth/2.1.3.RELEASE/single/spring-cloud-sleuth.html

##### Span（跨度）

+ 基本工作单元，发送一个远程调度任务就会产生一个Span
+ Span是一个64位唯一标识的ID，Trace是用另一个64位ID来唯一标识
+ Span 还有其他数据信 息，比如摘要、时间戳事件、Span 的 ID、以及进度 ID

##### Trace（跟踪）

+ 一系列 Span组成的一个树状结构
+ 请求一个微服务系统的API接口需要调用多个微服务，调用每个微服务都会产生一个新的Span，所有**由这个请求产生的Span组成了这个Trace**

##### Annotation（标注）

+ 用来及时记录一个事件的，一些核心注解用来定义一个请求的开 始和结束
+ 这些注解包括以下
  + cs【Client Sent】：客户端发送一个请求，这个注解描述了这个**Span的开始**
  + sr【Server Received】：**服务端获得请求**并准备开始处理它，sr-cs便可得到**网络传输的时间**
  + ss【Server Sent】 （服务端发送响应）：表明**请求处理的完成**(当请求返回客户端)，ss-sr就可以**得到服务器请求的时间**
  + cr【Client Received】 （客户端接收响应）：此时**Span的结束**，cr-cs便可以**得到整个请求所消耗的时间**

##### 示例

+ 如果服务调用顺序如下

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240128161646816.png" alt="image-20240128161646816" style="zoom:67%;" />.

  - 用以上概念完整的表示出来如下

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240128161800636.png" alt="image-20240128161800636" style="zoom:80%;" />.

  - Span之间的父子关系如下

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240128161909282.png" alt="image-20240128161909282" style="zoom:67%;" />.

#### 整合Sleuth+zipkin

##### Sleuth环境搭建

1. 服务提供者与消费者都要导入依赖【所以之间导入common模块】

   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-sleuth</artifactId>
   </dependency>
   ```

2. 配置debug 日志 

   ```properties
   logging.level.org.springframework.cloud.openfeign: debug
   logging.level.org.springframework.cloud.sleuth: debug
   ```

3. 发起一次远程调用，观察控制台

##### zipkin环境搭建

1. docker安装zipkin服务器`docker run -d -p 9411:9411 openzipkin/zipkin`

2. 导入依赖【zipkin 依赖也同时包含了 sleuth，可以省略 sleuth 的引用】

   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-zipkin</artifactId>
       <version>2.2.8.RELEASE</version>
   </dependency>
   ```

3. 添加zipkin相关配置

   ```yaml
   zipkin:
     base-url: http://192.168.32.100:9411/ # zipkin 服务器的地址
   # 关闭服务发现，否则 Spring Cloud 会把 zipkin 的 url 当做服务名称
     discoveryClientEnabled: false
     sender:
       type: web # 设置使用 http 的方式传输数据
   sleuth:
     sampler:
       probability: 1 # 设置抽样采集率为 100%，默认为 0.1，即 10%
   ```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240128171519954.png" alt="image-20240128171519954" style="zoom: 67%;" />.

##### zipkin数据持久化【了解】

- https://github.com/openzipkin/zipkin#storage-component

- 支持的存储方式：内存（默认）、MySQL、Elasticsearch、 Cassandra

- 使用 Elasticsearch 作为 Zipkin 的存储数据库的官方文档

  - elasticsearch-storage： https://github.com/openzipkin/zipkin/tree/master/zipkin-server#elasticsearch-storage 
  - zipkin-storage/elasticsearch https://github.com/openzipkin/zipkin/tree/master/zipkin-storage/elasticsearch

- 通过 docker的方式设置存储类型为es的镜像

  `docker run --env STORAGE_TYPE=elasticsearch --env ES_HOSTS=虚拟机ip:9200 openzipkin/zipkin-dependencies`

- 存储类型为es的zipkin支持的环境变量

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240128172142285.png" alt="image-20240128172142285" style="zoom:67%;" />.

- 使用es时 Zipkin Dependencies支持的环境变量

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240128172052502.png" alt="image-20240128172052502" style="zoom: 80%;" />.

## 性能与压力测试

### 压力测试

- 压力测试考察当前软硬件环境下系统所能承受的最大负荷并帮助找出系统瓶颈所在
- 压测都是为了系统在线上的处理能力和稳定性维持在一个标准范围内
- 使用压力测试，有希望找到很多种用其他测试方法更难发现的错误，有两种错误类型是：**内存泄漏，并发与同步**
- 有效的压力测试系统将应用以下这些关键条件：**重复，并发，量级，随机变化**

#### 性能指标

- 响应时间（Response Time: RT）：指用户从客户端发起一个请求开始，到客户端接收到从服务器端返回的响应结束，所耗费的时间

  - 最大响应时间（Max Response Time） 指用户发出请求或者指令到系统做出反应（响应） 的最大时间
  - 最少响应时间（Mininum ResponseTime） 指用户发出请求或者指令到系统做出反应（响 应）的最少时间
  - 90%响应时间（90% Response Time） 是指所有用户的响应时间进行排序，第90%的响应时间

- HPS、TPS、QPS

  - HPS（Hits Per Second） ：每秒点击次数，单位是次/秒

  - TPS（Transaction per Second）：系统每秒处理交易数，单位是笔/秒

  - QPS（Query per Second）：系统每秒处理查询次数，单位是次/秒

  - 对于互联网业务中，如果某些业务有且仅有一个请求连接，那么 TPS=QPS=HPS，一 般情况下用 TPS 来衡量整个业务流程，用 QPS 来衡量接口查询次数，用 HPS 来表示对服务器单击请求

  - 无论 TPS、QPS、HPS,此指标是衡量系统处理能力非常重要的指标，越大越好，根据经验，一般情况下： 

    - 金融行业：1000TPS~50000TPS，不包括互联网化的活动 

    - 保险行业：100TPS~100000TPS，不包括互联网化的活动 

    - 制造行业：10TPS~5000TPS 互联网电子商务：10000TPS~1000000TPS 
    - 互联网中型网站：1000TPS~50000TPS 互联网小型网站：500TPS~10000TPS

- 从外部看，性能测试主要关注如下三个指标 

  - 吞吐量：每秒钟系统能够处理的请求数、任务数
  - 响应时间：服务处理一个请求或一个任务的耗时
  - 错误率：一批请求中结果出错的请求所占比例

#### JMeter

##### 入门

- 安装：https://jmeter.apache.org/download_jmeter.cgi 下载对应的压缩包，解压运行 jmeter.bat 即可【直接去镜像下载】

1. 创建测试计划，右键测试计划添加线程组

   ![image-20231122144412834](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122144412834.png).

2. 设置线程属性

   - 线程数：虚拟用户数，一个虚拟用户占用一个进程或线程，设置多少虚拟用户数在这里也就是设置多少个线程数
   - Ramp-Up Period(in seconds)准备时长：设置的虚拟用户数需要多长时间全部启动
     - 如果线程数为 10，准备时长为 2，那么需要 2 秒钟启动 10 个线程，也就是每秒钟启动 5 个 线程
   - 循环次数：每个线程发送请求的次数
     - 如果线程数为 10，循环次数为 100，那么每个线 程发送 100 次请求，总请求数为 10*100=1000
     - 如果勾选了“永远”，那么所有线程会 一直发送请求，一到选择停止运行脚本
   - Delay Thread creation until needed：直到需要时延迟线程的创建
   - 调度器：设置线程组启动的开始时间和结束时间(配置调度器时，需要勾选循环次数永远)
   - 持续时间（秒）：测试持续时间，会覆盖结束时间
   - 启动延迟（秒）：测试延迟启动时间，会覆盖启动时间
   - 启动时间：测试启动时间，启动延迟会覆盖它
   - 结束时间：测试结束时间，持续时间会覆盖它

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122144514462.png" alt="image-20231122144514462" style="zoom: 80%;" />.

3. 添加取样器，测试http请求

   ![image-20231122144613894](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122144613894.png).

4. 监听请求结果

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122144739044.png" alt="image-20231122144739044" style="zoom: 80%;" />.

5. <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122145423950.png" alt="image-20231122145423950" style="zoom:67%;" />清空之前报告的记录

6. 结果分析

   - Throughput 吞吐量每秒请求的数大于并发数，则可以慢慢的往上面增加；若在压测的机 器性能很好的情况下，出现吞吐量小于并发数，说明并发数不能再增加了，可以慢慢的往下减，找到最佳的并发数
   - 最大的tps，不断的增加并发数，加到 tps 达到一定值开始出现下降，那么那个值就是最大的 tps
   - 最大的并发数：最大的并发数和最大的 tps 是不同的概率，一般不断增加并发数，达到一个值后，服务器出现请求超时，则可认为该值为最大的并发数
   - 压测过程出现性能瓶颈，若压力机任务管理器查看到的 cpu、网络和 cpu 都正常，未达到 90%以上，则可以说明服务器有问题，压力机没有问题
   - 影响性能考虑点包括： 数据库、应用程序、中间件（tomact、Nginx）、网络和操作系统等方面
   - 首先考虑自己的应用属于**CPU密集型【大量计算、排序】**还是**IO密集型【大量读写、网络流量】**

##### JMeter Address Already in use 错误解决

- windows 本身提供的端口访问机制的问题：Windows 提供给 TCP/IP 链接的端口为 1024-5000，并且要四分钟来循环回收他们。就导致在短时间内跑大量的请求时将端口占满了

- 帮助文档：https://support.microsoft.com/zh-cn/help/196271/when-you-try-to-connect-from-tcp-ports-greater-than-5000-you-receive-t 

- 解决步骤

  1. cmd 中，用 regedit 命令打开注册表 

  2. 在 HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters 下

     1. 右击 parameters，添加一个新的 DWORD，名字为 MaxUserPort
     2. 然后双击MaxUserPort，输入数值数据为65534，基数选择十进制（如果是分布式运行的话，控制机器和负载机器都需要这样操作）

     <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122151800026.png" alt="image-20231122151800026" style="zoom:67%;" />.

  3. 添加TCPTimedWaitDelay：30【设置多次时间可以关闭没用过的端口，关闭的端口可以重复使用】

     <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122151940778.png" alt="image-20231122151940778" style="zoom:67%;" />.

  4. 修改配置完毕之后记得重启机器才会生效 

### 性能监控

#### jvm内存模型

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122154450756.png" alt="image-20231122154450756" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122154831454.png" alt="image-20231122154831454" style="zoom:67%;" />

- 程序计数器 Program Counter Register
  - 记录的是正在执行的虚拟机字节码指令的地址
  - 此内存区域是唯一一个在JAVA虚拟机规范中没有规定任何OutOfMemoryError的区域
- 虚拟机：VM Stack 
  - 描述的是 JAVA方法执行的内存模型，每个方法在执行的时候都会创建一个栈帧， 用于存储局部变量表，操作数栈，动态链接，方法接口等信息
  - 局部变量表存储了编译期可知的各种基本数据类型、对象引用
  - 线程请求的栈深度不够会报 StackOverflowError 异常
  - 栈动态扩展的容量不够会报 OutOfMemoryError 异常
  - 虚拟机栈是线程隔离的，即每个线程都有自己独立的虚拟机栈
- 本地方法：Native Stack，本地方法栈类似于虚拟机栈，只不过本地方法栈使用的是本地方法
- 堆：Heap，几乎所有的对象实例都在堆上分配内存

#### 堆

- 所有的**对象实例以及数组**都要在堆上分配

- 堆是垃圾收集器管理的主要区域，也被称为“GC 堆”；也是优化最多考虑的地方

- 堆可以细分为

  - 新生代
    - Eden 空间
    - From Survivor 空间
    - To Survivor 空间
  - 老年代
  - 永久代/元空间
    - Java8 以前永久代，受 jvm管理
    - java8 以后元空间，直接使用物理内存。因此， 默认情况下，元空间的大小仅受本地内存限制

- 垃圾回收

  - 从 Java8 开始，HotSpot已经完全将永久代移除，取而代之的是一 个新的区域—元空间

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122155126565.png" alt="image-20231122155126565" style="zoom:67%;" />.

  - 步骤理解

    1. 新对象都是要进入新生代的，如果此时eden没有空间就要进行一次垃圾回收

       - 没有用到的对象被回收

       - 有用到的对象放在幸存者区

    2. 新对象

       1. 如果eden还是放不下，就认为新对象是大对象，将其放进老年代
       2. 如果老年代无法容纳，那就进行一次全面gc【性能慢，一定要避免经常性发送fgc】，将内存中所有未用过的对象清除掉
       3. 如果还放不下就报内存溢出异常

    3. 旧对象，如果在幸存者区

       - 放得下就放，对象存放超过阈值就移动到老年代

       - 放不下直接放到老年代

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122155344375.png" alt="image-20231122155344375" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122155355660.png" alt="image-20231122155355660" style="zoom:67%;" />

### jconsole与jvisualvm

#### 前置知识

- Jdk 的两个小工具 jconsole、jvisualvm（升级版的 jconsole）;通过命令行启动，可监控本地和远程应用（远程应用需要配置）

- 控制台输入jconsole/jvisualvm后点击连接的线程就可以监控了

- jvisualvm作用

  - 监控内存泄露，跟踪垃圾回收，执行时内存、cpu 分析，线程分析

  ![image-20231122162014456](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122162014456.png).

  - 运行：正在运行的 
  - 休眠：sleep 
  - 等待：wait 
  - 驻留：线程池里面的空闲线程 
  - 监视：阻塞的线程，正在等待锁

#### 安装插件方便查看gc

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122162205619.png" alt="image-20231122162205619" style="zoom:67%;" />=》<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122162434236.png" alt="image-20231122162434236" style="zoom:67%;" />=》安装完成重启jvisualvm

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122162634575.png" alt="image-20231122162634575" style="zoom:67%;" />.

### 压测

#### 中间件对性能的影响

##### 测试nignx

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122173927229.png" alt="image-20231122173927229" style="zoom:67%;" />.

- 监控nginx：docker stats

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122174328675.png" alt="image-20231122174328675" style="zoom:67%;" />.

![image-20231122174627822](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122174627822.png)

##### 测试网关

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122175736526.png" alt="image-20231122175736526" style="zoom:67%;" />![image-20231122175836570](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122175836570.png)

##### 测试简单服务

```java
@ResponseBody
@GetMapping("/hello")
public   String hello(){
    return "hello";
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122180259780.png" alt="image-20231122180259780" style="zoom:80%;" />.

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122180407459.png" alt="image-20231122180407459" style="zoom:67%;" />.

![image-20231122180424207](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122180424207.png)

##### 网关加简单请求

```yaml
- id: product_route
  uri: lb://gulimall-product
  predicates:
    - Path=/api/product/**,/hello
  filters:
    - RewritePath=/api/product?(?<segment>.*),/gulimalProduct/$\{segment}
```

![image-20231122182301690](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122182301690.png)

##### 全链路

- nginx+网关+简单服务	

![image-20231122183008813](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231122183008813.png)

#### 业务吞吐量【非全链路】

##### 测试一级分类

- 慢的原因：db、thymeleaf

![image-20231123160101920](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123160101920.png)

##### 测试三级分类

- 慢的原因：db

![image-20231123160404700](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123160404700.png)

##### 首页全量数据

- 勾选包含页面的所有资源

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123160726308.png" alt="image-20231123160726308" style="zoom:67%;" />.

- 测试结果，慢的原因：静态资源

![image-20231123161331981](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123161331981.png)

#### 结论

- 中间件越多，性能损失越大，大多都损失在网络交互

- 业务中对性能的影响因素

  - db（详情参照mysql优化课程）
    - 给查询字段parent_cid加索引
  - 模板的渲染速度（可以开启模板的缓存）

  ![image-20231123162319360](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123162319360.png)

  - 静态资源

### 优化

#### nginx动静分离

##### 需求

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231121200424492.png" alt="image-20231121200424492" style="zoom:67%;" />.

- 所有请求都要经过nginx
- 静态资源直接在nginx中返回
  - 以后将所有项目的静态资源都应该放在nginx里面
  - 规则：/static/**所有请求都由nginx直接返回
- 动态资源代理给网关去路由到具体服务

##### 步骤

1. 在nginx的html目录中创建static文件用于存放静态资源

2. 将需要用到的资源放进该目录

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123164404484.png" alt="image-20231123164404484" style="zoom:67%;" />.

3. 修改项目中的静态资源访问路径【全都加上/static前缀】

4. 在nginx的gulimall配置文件配置静态文件映射【映射到html目录】

   - 精准匹配要放在模糊匹配之前，静态资源就会直接在nignx的html目录中找

   ```shell
   location /static/{
       root /usr/share/nginx/html;
   }
   ```

5. 测试，gulimall域名访问不到静态资源，但是用nginx端口可以访问

   - 原因：**配置路径映射把nginx写成了nignx**

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123172803724.png" alt="image-20231123172803724" style="zoom: 50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123172831565.png" alt="image-20231123172831565" style="zoom: 33%;" />

6. 测试成功

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123172947735.png" alt="image-20231123172947735" style="zoom: 50%;" />.

##### 压测

- nginx会返回静态数据，所以只压测商品服务首页动静分离后的动态数据数据

![image-20231123174633126](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123174633126.png)

#### 调整堆内存

##### 配置

- -Xmn512m：将新生代调整到512m
- -Xms1024m：最大堆大小为1024m
- -Xmx1024m：初始堆大小为1024m，所以初始就固定为最大堆内存

##### 压测

- 模拟每秒200个请求进行压测

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123185630370.png" alt="image-20231123185630370" style="zoom:67%;" />![image-20231123185800079](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123185800079.png)

#### 优化三级分类接口

1. 将数据库的多次查询变为一次

   1. 查询所有记录

      ```java
      List<CategoryEntity> selectList = baseMapper.selectList(new QueryWrapper<>(null));
      ```

   2. 抽取**获取指定父id的对应的所有子节点**的公共方法

      ```java
      public List<CategoryEntity> getParentId(List<CategoryEntity> selectList,Long parentCid){
          List<CategoryEntity> collect = selectList.stream().filter(item -> {
              return item.getParentCid() == parentCid;
          }).collect(Collectors.toList());
          return collect;
      }
      ```

   3. 修改业务方法

      ```java
      public Map<String, List<Catelog2Vo>> getCatelogJsonFromDb() {
          //查询所有
          List<CategoryEntity> selectList = baseMapper.selectList(new QueryWrapper<>(null));
          //1.查出所有一级分类
          List<CategoryEntity> levelFirst = getParentId(selectList, 0l);
          //封装数据
          Map<String, List<Catelog2Vo>> map = levelFirst.stream().
              collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
              //2.查询每个一级分类的所有二级分类
              List<CategoryEntity> categoryEntities = 
                  getParentId(selectList, v.getCatId());//将当前分类的id作为父id，去查询其子节点
              List<Catelog2Vo> catelog2VoList = null;
              if (categoryEntities != null) {
                  catelog2VoList = categoryEntities.stream().map(level2 -> {
                      Catelog2Vo catelog2Vo = new Catelog2Vo
                              (v.getCatId().toString(), null, 
                               level2.getCatId().toString(), level2.getName());
                      //3.查询当前二级分类的三级分类
                      List<CategoryEntity> catelog3List = getParentId(selectList, level2.getCatId());
                      if (catelog3List != null) {
                          //封装成指定格式
                          List<Catelog2Vo.Catelog3Vo> catelog3VoList = 
                              catelog3List.stream().map(level3 -> {
                              Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo
                                      (level2.getCatId().toString(),
                                              level3.getCatId().toString(), level3.getName());
                              return catelog3Vo;
                          }).collect(Collectors.toList());
                          catelog2Vo.setCatalog3List(catelog3VoList);
                      }
                      return catelog2Vo;
                  }).collect(Collectors.toList());
              }
              return catelog2VoList;
          }));
          return map;
      }
      ```

2. 压测

   - 控制变量，堆内存配置改成之前的配置
   - 吞吐量从36.6提升到178.2

![image-20231123191324533](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123191324533.png)

## ！！！缓存与分布式锁

### 缓存 

#### 基础知识

##### 缓存使用 

- 为了系统性能的提升，一般都会将部分数据放入缓存中，加速访问，而**db承担数据落盘工作**
- 适合放入缓存数据
  - 即时性、数据一致性要求不高的【如物流信息】
  - 访问量大且更新频率不高的数据（读多，写少） 
  - 举例：电商类应用，商品分类，商品列表等适合缓存并加一个失效时间(根据数据更新频率来定)，后台如果发布一个商品，买家需要 5 分钟才能看到新的商品一般还是可以接受的

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123192021468.png" alt="image-20231123192021468" style="zoom:67%;" />.

##### 本地缓存

- 项目中可以使用map来缓存数据【本地缓存】

```java
HashMap<String,Object> cache=new HashMap<>();
data = cache.get(id);//从缓存加载数据
    if(data == null){
        data = db.load(id);//从数据库加载数据
        cache.put(id,data);//保存到 cache 中
    }
return data
```

- 本地缓存的组件和所在代码**属于同一进程**，运行在同一项目

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123192558027.png" alt="image-20231123192558027" style="zoom: 50%;" />.

- 本地模式在分布式下的问题
  - 分布式中**同类的不同服务各自维护自己的缓存**，在A服务中有的缓存，B服务无法读取到，如果负载均衡到不同服务就得重新读库
  - 会出现同类不同服务**缓存不一致**的情况，修改某个服务的缓存，无法同步修改其它服务

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123192829157.png" alt="image-20231123192829157" style="zoom: 50%;" />.

##### 分布式缓存

- 同类服务共享同一个缓存中间件

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123193244867.png" alt="image-20231123193244867" style="zoom: 50%;" />.

#### 整合redis

##### 环境搭建

1. 引入 redis-starter 

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-redis</artifactId>
   </dependency>
   ```

2. 配置redis连接主机

   ```yaml
   spring:  
       redis:
           host: 192.168.32.100
           port: 6379
   ```

3. 使用springboot自动配置好的RedisTemplate操作redis 

   ```java
   @Autowired
   StringRedisTemplate stringRedisTemplate;
   @Test
   public void testStringRedisTemplate(){
       ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
       ops.set("hello","world"+ UUID.randomUUID());
       String hello = ops.get("hello");
       System.out.println(hello);
   }
   ```

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123195559848.png" alt="image-20231123195559848" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123195714845.png" alt="image-20231123195714845" style="zoom: 67%;" />

#### 修改业务功能

1. 业务方法加上缓存，原业务方法改为getCatelogJsonFromDb

   ```java
   public Map<String, List<Catelog2Vo>> getCatelogJson() {
       //1.查询缓存是否有分类数据
       String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
       if(StringUtils.isEmpty(catalogJSON)){
           //1.1如果缓存中没有就要查询数据库
           Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatelogJsonFromDb();
           //1.2将查询到的数据转成json字符串放入缓存【json跨语言跨平台兼容】
           catalogJSON = JSON.toJSONString(catelogJsonFromDb);
           redisTemplate.opsForValue().set("catalogJSON",catalogJSON);
       }
       //2.将缓存获取到的json字符串转成map对象
       Map<String, List<Catelog2Vo>> result =
               JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
       });
       return result;
   }
   ```

2. 测试

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123201703794.png" alt="image-20231123201703794" style="zoom:67%;" />.

3. 压测【178.5】

![image-20231124204927563](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124204927563.png)

- **旧版本**报堆外内存异常【我这边是没有报错，**了解**】
  - 产生原因
    - springboot2以后默认使用lettuce作为操作redis的客户端，它使用netty进行网络通信
    - netty如果没有指定堆外内存，默认就为项目最大内存，可以通过-Dio.netty.maxDirectMemory进行设置
  - 解决方案
    - 升级lettuce客户端【现在已经解决了】
    - 切换其它客户端

![image-20231123202351912](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231123202351912.png)

### 缓存失效问题

#### 缓存穿透

- 缓存穿透是指查询一个一定不存在的数据，由于缓存是不命中，将去查询数据库，但是数据库也无此记录，没有将这次查询的null写入缓存，这将导致这个不存在的数据每次请求都要到存储层去查询，失去了缓存的意义
- 在流量大时，可能 DB 就挂掉了，要是有人利用不存在的 key 频繁攻击我们的应用，这就是漏洞
- 解决： 缓存空结果、并且设置短的过期时间、布隆过滤器

#### 缓存雪崩 

- 缓存雪崩是指在我们设置缓存时采用了相同的过期时间，导致缓存在**某一时刻同时失效**，请求全部转发到 DB，DB 瞬时压力过重雪崩
- 解决
  - 原有的失效时间基础上增加一个随机值，这样每一个缓存的过期时间的重复率就会降低，就很难引发集体失效的事件
  - 缓存集群、多缓存结合

#### 缓存击穿

- 对于一些设置了过期时间的 key，如果这些 key 可能会在某些时间点被超高并发地访问， 是一种非常**“热点”数据**，这个时候，需要考虑一个问题：如果这个key在**大量请求同时进来前正好失效**，那么所 有对这个 key 的数据查询都落到db，称为缓存击穿
- 解决： 双检加锁

### 分布式锁

#### 本地锁

##### 给当前商品服务加本地锁

- 当前项目可以使用本地锁，使用本地锁进行双检加锁，而且要考虑时序问题，**查数据库和写入缓存**应该放在同一个方法

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124210907897.png" alt="image-20231124210907897" style="zoom:50%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124204809396.png" alt="image-20231124204809396" style="zoom:67%;" />

```java
public Map<String, List<Catelog2Vo>> getCatelogJsonFromDb() {
    synchronized (this){
        //得到锁之后，再去缓存中查询一次【双检】
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if(StringUtils.isEmpty(catalogJSON)){
            //1.1如果缓存中没有就要查询数据库
            Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatelogJsonFromDb();
            //1.2将查询到的数据转成json字符串放入缓存【json跨语言跨平台兼容】
            catalogJSON = JSON.toJSONString(catelogJsonFromDb);
            redisTemplate.opsForValue().set("catalogJSON",catalogJSON);
            return catelogJsonFromDb;
        }
        //查询所有
        List<CategoryEntity> selectList = baseMapper.selectList(new QueryWrapper<>(null));
        //1.查出所有一级分类
        List<CategoryEntity> levelFirst = getParentId(selectList,0l);
        //封装数据
        Map<String, List<Catelog2Vo>> map = levelFirst.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //2.查询每个一级分类的所有二级分类
            List<CategoryEntity> categoryEntities = getParentId(selectList,v.getParentCid());
            List<Catelog2Vo> catelog2VoList = null;
            if (categoryEntities != null) {
                catelog2VoList = categoryEntities.stream().map(level2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo
                            (v.getCatId().toString(), null, level2.getCatId().toString(), level2.getName());
                    //3.查询当前二级分类的三级分类
                    List<CategoryEntity> catelog3List = getParentId(selectList,level2.getParentCid());
                    if(catelog3List!=null){
                        //封装成指定格式
                        List<Catelog2Vo.Catelog3Vo> catelog3VoList = catelog3List.stream().map(level3-> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo
                                    (level2.getCatId().toString(),level3.getCatId().toString(),level3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catelog3VoList);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2VoList;
        }));
        //查到数据就写入缓存
        catalogJSON = JSON.toJSONString(map);
        redisTemplate.opsForValue().set("catalogJSON",catalogJSON);
        return map;
    }
}
```

- 压测结果

![image-20231124204648261](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124204648261.png)

##### 本地锁在分布式下的问题

1. 复制多个商品服务

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124205710447.png" alt="image-20231124205710447" style="zoom:67%;" />=指定不同商品服务的端口号=》<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124205807507.png" alt="image-20231124205807507" style="zoom:67%;" />

2. 同时启动多个商品服务

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124210000268.png" alt="image-20231124210000268" style="zoom:67%;" />.

3. 使用域名访问商品服务，最后会通过网关负载均衡给不同的商品服务

4. 测试结果：每个商品服务都查询过数据库

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124210558930.png" alt="image-20231124210558930" style="zoom:67%;" />.

![image-20231124210648347](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124210648347.png)

- 结论：本地锁，只能锁住当前进程，分布式情况需要分布式锁

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124202532372.png" alt="image-20231124202532372" style="zoom:67%;" />.

#### 分布式锁-自研

##### 基本原理

- 可以同时去一个地方“占坑”
  - 如果占到，就执行逻辑
  - 否则就必须等待，直到释放锁
  - “占坑”可以去redis，可以去数据库，可以去任何大家都能访问的地方
  - 等待可以自旋的方式

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124210952344.png" alt="image-20231124210952344" style="zoom: 50%;" />.

- 虚拟机中模拟分布式锁

  1. 先开启多个会话，然后向所有会话发送`docker exec -it redis redis-cli`命令

     <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124211741892.png" alt="image-20231124211741892" style="zoom:67%;" />==》<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124211907729.png" alt="image-20231124211907729" style="zoom:67%;" />

  2. 模拟多个客户端同时占锁，向所有会话发送`set lock hahah NX`

     - 只有一个会话可以抢到锁

     <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124212208943.png" alt="image-20231124212208943" style="zoom:67%;" />.

##### 演进一-使用redis锁

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124212434277.png" alt="image-20231124212434277" style="zoom: 67%;" />.

```java
public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedisLock(){
    Boolean lock=redisTemplate.opsForValue().setIfAbsent("lock","1111");
    if(lock){
        //加锁成功,执行业务
        Map<String, List<Catelog2Vo>> data = getCatelogJsonFromDb();
        redisTemplate.delete("lock");//解锁
        return data;
    }else {
        //加锁失败就重试
        //TODO 休眠
        return getCatelogJsonFromDbWithRedisLock();
    }
}
```

- 问题： setnx占好了位，业务代码异常或者程序在页面过程中宕机，没有执行删除锁逻辑，这就造成了死锁 
- 解决： **设置锁的自动过期**，即使没有删除，会自动删除

##### 演进二-设置过期时间

- 抢到锁后设置锁的过期时间`redisTemplate.expire("lock",30, TimeUnit.SECONDS);`

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124213810418.png" alt="image-20231124213810418" style="zoom: 50%;" />.

- 问题： setnx设置好，正要去设置过期时间，宕机，又死锁了
- 解决： **设置过期时间和占位必须是原子的**，redis支持使用setnx ex 命令

##### 演进三-过期时间和占位原子性

`Boolean lock=redisTemplate.opsForValue().setIfAbsent("lock","1111",300,TimeUnit.SECONDS);`

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124214346955.png" alt="image-20231124214346955" style="zoom: 50%;" />.

- 问题：  如果由于业务时间很长，自己的锁过期了，此时别人就可以抢新锁，如果我们直接删除，有可能把别人正在持有的锁删除
- 解决： 占锁的时候，值指定为uuid，每个人匹配到自己的锁才删除

##### 演进四-锁指定uuid

```java
String uuid= UUID.randomUUID().toString();
Boolean lock=redisTemplate.opsForValue().setIfAbsent("lock",uuid,300,TimeUnit.SECONDS);
if(lock){
    if(uuid.equals(uuidLock)){
        //判断是自己的锁才能删
        redisTemplate.delete("lock");//解锁
    }
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124214942804.png" alt="image-20231124214942804" style="zoom: 50%;" />.

- 问题： 如果正好判断是当前值，正要删除锁的时候，锁已经过期， 别人已经设置到了新的值，那么删除的是别人的锁 
- 解决： 删除锁必须保证原子性，使用redis+Lua脚本完成

##### 演进五-redis+Lua脚本

```java
public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedisLock(){
    //1.抢占分布式锁和设置过期时间原子性
    String uuid= UUID.randomUUID().toString();
    Boolean lock=redisTemplate.opsForValue().setIfAbsent("lock",uuid,300,TimeUnit.SECONDS);
    if(lock){
        //加锁成功,执行业务
        try {
            Map<String, List<Catelog2Vo>> data = getCatelogJsonFromDb();
            return data;
        }finally {
            //lua脚本原子解锁，无论业务成功与否都要解锁
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) else return 0 end";
            Long lock1 = redisTemplate.execute(new
                            DefaultRedisScript<Long>(script, Long.class)
                    , Arrays.asList("lock"), uuid);
        }
    }else {
        //加锁失败就重试
        //TODO 休眠
        return getCatelogJsonFromDbWithRedisLock();
    }
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124220517574.png" alt="image-20231124220517574" style="zoom:50%;" />.

- 只在商品服务2查询了数据库

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231124222119750.png" alt="image-20231124222119750" style="zoom: 67%;" />.

- todo，还有很多地方需要改进，比如说递归重试改成自旋，可重入锁等等

#### Redisson完成分布式锁

##### 简介 

- Redisson 是架设在 Redis 基础上的一个 Java 驻内存数据网格（In-Memory Data Grid）
- 充分利用了 Redis 键值数据库提供的一系列优势，基于 Java 实用工具包中常用接口，为使用者提供了一系列具有分布式特性的常用工具类，使得原本作为协调单机多线程并发程序的工具包获得了协调分布式多机多线程并发系统的能力，大大降低了设计和研发大规模分布式 系统的难度
- 同时结合各富特色的分布式服务，更进一步简化了分布式环境中程序相互之间的协作
- 官方文档：https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95

##### 环境搭建

1. 导入依赖，整合redisson作为分布式锁框架

   ```xml
   <dependency>
       <groupId>org.redisson</groupId>
       <artifactId>redisson</artifactId>
       <version>3.13.4</version>
   </dependency>
   ```

2. redisson配置类配置连接信息

   ```java
   @Configuration
   public class MyRedissonConfig {
       @Bean(destroyMethod = "shutdown")
       public RedissonClient redissonClient() {
           Config config = new Config();
           //可以用"rediss://"来启用 SSL 连接
           config.useSingleServer().setAddress("redis://192.168.32.100:6379");
           return Redisson.create(config);
       }
   }
   ```

3. 测试是否连接成功

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125132009187.png" alt="image-20231125132009187" style="zoom:67%;" />.

##### 测试简单加锁逻辑

1. 测试简单加锁逻辑

   - 锁默认设置了30s的过期时间
   - 业务未完成会自动续期

   ```java
   public String hello(){
       //1.获取锁，只要锁的名称相同就是同一把锁
       RLock myLock = redissonClient.getLock("myLock");
       //2.加锁
       myLock.lock();
       try {
           System.out.println("加锁成功执行业务"+Thread.currentThread().getId());
           Thread.sleep(30000);
       } catch (InterruptedException e) {
           e.printStackTrace();
       }finally {
           //3.解锁
           System.out.println("释放锁");
           myLock.unlock();
       }
       return "hello";
   }
   ```

2. `lock.lock(10, TimeUnit.SECONDS);`不会自动续期

   - 当业务时间比锁的时间长时，报错

     ```
     java.lang.IllegalMonitorStateException: attempt to unlock lock, not locked by current thread by node id: 5702e79a-789f-4116-91a4-13442408d3ba thread-id: 104
     	at org.redisson.RedissonLock.lambda$unlockAsync$3(RedissonLock.java:605)
     	at org.redisson.misc.RedissonPromise.lambda$onComplete$0(RedissonPromise.java:187)
     ```

   - 原理

     - 如果指定了超时时间，就会发送lua脚本给redis执行，并且设置锁的超时时间
     - 没指定超时时间，会先获取30s的看门狗默认超时时间，重复上述逻辑
       - 占锁成功就会启动一个定时任务重新给锁设置过期时间【看门狗默认超时时间】
       - 定时任务每10s执行一次<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125144655722.png" alt="image-20231125144655722" style="zoom:67%;" />

   - 雷神最佳实战：指定长一点的超时时间，不走自动续期的逻辑，如果业务执行太久也有问题，干脆就让锁超时丢掉

##### 测试读写锁

1. 编写简单读写方法

   ```java
   @ResponseBody
   @GetMapping("/write")
   public String writeValue() {
       String s = "";
       RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
       //1.写数据加写锁
       RLock writeLock = lock.writeLock();
       try {
           writeLock.lock();
           s = UUID.randomUUID().toString();
           System.out.println("加锁成功执行业务" + Thread.currentThread().getId());
           Thread.sleep(30000);
           redisTemplate.opsForValue().set("writeValue", s);
       } catch (InterruptedException e) {
           e.printStackTrace();
       } finally {
           writeLock.unlock();
       }
       return s;
   }
   @ResponseBody
   @GetMapping("/read")
   public String readValue() {
       RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
       //读数据使用读锁
       RLock readLock = lock.readLock();
       readLock.lock();
       String value = redisTemplate.opsForValue().get("writeValue");
       readLock.unlock();
       return value;
   }
   ```

2. 测试写的过程中读数据

   - 写数据时读取被阻塞
   - 当写完成时，可以读取到最新数据
   - 如果没有数据修改，读取不会被阻塞

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125150312605.png" alt="image-20231125150312605" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125150327556.png" alt="image-20231125150327556" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125150341093.png" alt="image-20231125150341093" style="zoom:67%;" />

3. 结论

   - 加了读写锁可以保证每次读取都是最新数据

   - 写锁是排他锁，读锁是共享锁，修改期间写锁没释放读锁必须等待

4. 测试读的时候写数据【先给读操作进行睡眠】

   - 只要有读锁，写就必须等待
   - 所有只要有写锁存在就必须等待，读写等待读锁，写读等待写锁，写写等待写锁

   <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125151425992.png" alt="image-20231125151425992" style="zoom:67%;" />.

##### 测试闭锁

- 所有任务都完成才释放锁

```java
@GetMapping("/lockDoor")
@ResponseBody
public String lockDoor() throws InterruptedException {
    RCountDownLatch door = redissonClient.getCountDownLatch("door");
    door.trySetCount(5l);//设置任务总量
    door.await();//等待闭锁都完成
    return "放假了....";
}
@ResponseBody
@GetMapping("/goHome/{id}")
public String goHome(@PathVariable Long id){
    RCountDownLatch door = redissonClient.getCountDownLatch("door");
    door.countDown();//计数-1，即完成一个任务
    return id+"班都走了";
}
```

- 只有当前任务量减到0才释放锁

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125152510846.png" alt="image-20231125152510846" style="zoom: 67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125152605180.png" alt="image-20231125152605180" style="zoom: 50%;" />

##### 测试信号量

- 需求，总共三个停车位，测试停车和离车
  - 停车得看是否有空余车位，占车位就减少一个信号量
  - 离车得看是否有停车，释放车位就增加一个信号量

```java
@GetMapping("/park")
@ResponseBody
public String parkCar() throws InterruptedException {
    RSemaphore park = redissonClient.getSemaphore("park");
    park.acquire();//获取一个车位【信号】，即占一个车位，减少一个信号量
    return "停车....";
}
@ResponseBody
@GetMapping("/go")
public String go(@PathVariable Long id){
    RSemaphore park = redissonClient.getSemaphore("park");
    park.release();//释放一个车位，即增加一个信号量
    return "离车....";
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125154137638.png" alt="image-20231125154137638" style="zoom: 60%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125154221491.png" alt="image-20231125154221491" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125154446796.png" alt="image-20231125154446796" style="zoom:67%;" />

- 分布式限流
  - acquire是阻塞式获取
  - tryAcquire是非阻塞式获取，没获取到就算了

```java
RSemaphore park = redissonClient.getSemaphore("park");
boolean b = park.tryAcquire();//获取一个车位【信号】，即占一个车位，减少一个信号量
if(b){
    //执行业务
    return "停车成功";
}else {
    return "车位已满，稍后再来";
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125155138060.png" alt="image-20231125155138060" style="zoom:67%;" />.

#### 分布式锁的细节

##### 锁定粒度

- 锁的粒度越细，锁的资源就越少，运行起来就越快，所有给每种业务都起不同的锁名称
- 如果锁某个商品，那统一命名为product-商品id-lock

##### 缓存数据一致性

###### 双写模式

- 问题：由于卡顿等原因，导致写缓存2在前，写缓存1在后面就出现了不一致【即原本更新的缓存又变成脏数据了】
- 解决
  - 写的时候加锁
  - 暂时的数据不一致，给缓存设置过期时间【前提是读取脏数据要有个**容忍时间**】
    - 这是暂时性的脏数据问题，但是在数据稳定，缓存过期以后，又能得到最新的正确数据
    - 最后要保证最终一致性

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125160319074.png" alt="image-20231125160319074" style="zoom: 50%;" />.

###### 失效模式

- 问题：存在读写产生数据不一致，即写数据库时，正好有读数据库的需求，当写操作删完缓存，读操作也将旧数据重新写入缓存
- 解决
  - **经常修改**的数据直接读数据库
  - 缓存的所有数据都有**过期时间**，数据过期下一次查询触发主动更新【当前系统采取的一致性解决方案】
  - 读写数据的时候，加上分布式的**读写锁**，对经常写的数据有较大性能影响【当前系统采取的一致性解决方案】

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125160616267.png" alt="image-20231125160616267" style="zoom: 50%;" />.

###### 解决方案

- 无论是双写模式还是失效模式，**都会导致缓存的不一致问题**，即多个实例同时更新会出事
  - 如果是**用户纬度**数据（订单数据、用户数据），这种**并发几率非常小**，不用考虑这个问题，缓存数据加上过期时间，每隔一段时间触发读的主动更新即可 
  - 如果是菜单，商品介绍等**基础数据**，容忍大程度的缓存不一致，也可以去使用canal订阅binlog的方式
  - **缓存数据+过期时间**也足够解决大部分业务对于缓存的要求
  - 通过加锁保证并发读写，写写的时候按顺序排好队，读读无所谓，所以适合**使用读写锁**（业务不关心脏数据，允许临时脏数据可忽略）
- 总结
  - 能放入缓存的数据本就不应该是实时性、一致性要求超高的
  - 缓存数据的时候加上过期时间，保证每天拿到当前最新数据即可
  - 不应该过度设计，增加系统的复杂性
  - 遇到**实时性、一致性要求高**的数据就应该**查数据库**，即使慢点

###### Canal

- 使用Canal更新缓存

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125170423966.png" alt="image-20231125170423966" style="zoom:67%;" />.

- 使用Canal解决数据异构【大数据推荐】

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231125170437621.png" alt="image-20231125170437621" style="zoom:67%;" />.

### Spring Cache

#### 简介

- Spring从 3.1开始定义了Cache和CacheManager接口来统一不同的缓存技术，并支持使用 JCache（JSR-107）注解简化我们开发 
- Cache 接口为缓存的组件规范定义，包含缓存的各种操作集合
  - Cache接口下Spring提供了各种 xxxCache的实现，如RedisCache ， EhCacheCache , ConcurrentMapCache等
- 每次调用需要缓存功能的方法时，Spring 会检查检查指定参数的指定的目标方法是否已经被调用过
  - 如果有就直接从缓存中获取方法调用后的结果
  - 如果没有就调用方法并缓存结果后返回给用户，**下次调用直接从缓存中获取**
- 使用 Spring 缓存抽象时我们需要关注以下两点
  - 确定方法需要被缓存以及他们的缓存策略
  - 从缓存中读取之前缓存存储的数据

#### 基础概念

##### 缓存管理器图解

- 一个缓存管理器可以管理多个缓存【缓存管理器是定义规则的】，用缓存组件来操作某个缓存

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126135250636.png" alt="image-20231126135250636" style="zoom:67%;" />.

##### 注解

- @Cacheable：触发将数据**保存**到缓存的操作
- @CacheEvict：触发将数据从缓存**删除**的操作，失效模式可以使用
- @CachePut：方法执行后的结果**更新**缓存，双写模式可以使用
- @Caching：组合以上多个操作
- @CacheConfig：在类级别**共享缓存的相同配置**

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126135728374.png" alt="image-20231126135728374" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126135752115.png" alt="image-20231126135752115" style="zoom: 75%;" />

##### 表达式语法

![image-20231126135842487](C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126135842487.png).

#### 整合springCache

1. 导入依赖【如果使用redis作为缓存需要引入redis的启动场景】

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-cache</artifactId>
   </dependency>
   ```

2. 配置

   - 自动配置的内容

     - `CacheAuroConfiguration`会导入`RedisCacheConfiguration`等不同类型缓存的配置类
     - 自动配置了缓存管理器

   - 需要配置的内容

     - 缓存的类型`spring.cache.type=redis`

     - 指定缓存前缀并且使用缓存前缀【默认使用】

       - 如果指定了缓存前缀就用指定的，没有就默认使用缓存的名字作为前缀
       - 如果不适用缓存前缀，就不会用指定的前缀或者缓存的名字作为前缀

       ```java
       spring.cache.redis.key-prefix=CACHE_
       spring.cache.redis.use-key-prefix=true
       ```

     - 开启缓存空值`spring.cache.redis.cache-null-values=true`

3. 开启缓存功能`@EnableCaching`

4. 测试缓存注解

   ```java
   //表示当前方法的结果需要缓存，如果缓存中有，不用调用方法，如果缓存中没有就调用方法并将结果放入缓存
   @Cacheable({"category"}) //指定数据需要放入的缓存分区【推荐按照业务类型分】
   @Override
   public List<CategoryEntity> getLevelFirst() {
       List<CategoryEntity> categoryEntities = baseMapper.selectList(
               new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
       return categoryEntities;
   }
   ```

   - 第一次访问调用方法并且将数据序列化后存入redis

     <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126142357451.png" alt="image-20231126142357451" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126142436429.png" alt="image-20231126142436429" style="zoom:67%;" />

   - 第二次访问没有调用方法

     <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126142533816.png" alt="image-20231126142533816" style="zoom:67%;" />.

#### @Cacheable细节设置

##### @Cacheable的默认行为

- 缓存中有结果就不调用方法
- 缓存的key是默认自动生成的，命名规则为：`缓存的名字::SimpleKey []`
- 缓存的value值：默认使用jdk序列化机制，加序列化之后的数据存到redis
- 默认ttl为-1

##### 自定义操作

- 指定缓存的key名称：@Cacheable的key属性接收一个表达式

  - 指定字符串

    - `@Cacheable(value = {"category"},key = "'level1Categorys'")`
    - 字符串需要用''括起来，否则会被当成表达式

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126143836007.png" alt="image-20231126143836007" style="zoom:80%;" />.

  - 使用表达式语法

    - `@Cacheable(value = {"category"},key = "#root.methodName.substring(3)")`

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126144524350.png" alt="image-20231126144524350" style="zoom:80%;" />.

- 指定ttl：配置文件配置过期时间`spring.cache.redis.time-to-live=60000`

  - 单位毫秒

  <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126143859281.png" alt="image-20231126143859281" style="zoom:80%;" />.

- 将数据以json格式存入，需要自定义管理器

##### 自定义管理器

###### 管理器启动流程

1. `CacheAuroConfiguration`会导入`RedisCacheConfiguration`
2. `RedisCacheConfiguration`向容器注入了缓存管理器组件
3. 缓存管理器初始化所有缓存，每个缓存决定使用的配置
   - 如果有自定义的`RedisCacheConfiguration`，就使用自定义的
   - 没有就用默认的

- 如果修改管理器配置只需要给容器注入一个自定义的`RedisCacheConfiguration`，就会将配置应用到缓存管理器的所有分区

###### 修改缓存的值用json方式存储

```java
@Bean
RedisCacheConfiguration redisCacheConfiguration(){
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
    config=config.serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()
            ));
    config=config.serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer()
    ));
    return config;
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126150116374.png" alt="image-20231126150116374" style="zoom:67%;" />.

- 问题：配置文件的配置项无法启用

  1. 需要开启属性配置的绑定功能，指定要绑定的属性配置类
     - `@EnableConfigurationProperties(ConfigurationProperties.class)`

  2. 使用属性配置类
     - 自动注入
     - 将属性配置类作为方法参数【因为@Bean就是给容器放组件，方法传的所有参数都会从容器中确定】

```java
RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties){
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
    config=config.serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()
            ));
    config=config.serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer()
    ));
    CacheProperties.Redis redisProperties = cacheProperties.getRedis();
    if (redisProperties.getTimeToLive() != null) {
        config = config.entryTtl(redisProperties.getTimeToLive());
    }
    if (redisProperties.getKeyPrefix() != null) {
        config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
    }
    if (!redisProperties.isCacheNullValues()) {
        config = config.disableCachingNullValues();
    }
    if (!redisProperties.isUseKeyPrefix()) {
        config = config.disableKeyPrefix();
    }
    return config;
}
```

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126151600107.png" alt="image-20231126151600107" style="zoom:67%;" />.

#### @CacheEvict

- 失效模式的使用，在修改的时候清除缓存

```java
@Transactional
@CacheEvict(value = "category",key="'LevelFirst'")
@Override
public void updateCascade(CategoryEntity category) {
    this.updateById(category);
    categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
}
```

- 测试修改

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126153706281.png" alt="image-20231126153706281" style="zoom:67%;" />==》<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126153735279.png" alt="image-20231126153735279" style="zoom:67%;" />==》<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126153753089.png" alt="image-20231126153753089" style="zoom:67%;" />==》<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126153816967.png" alt="image-20231126153816967" style="zoom: 50%;" />==》<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126153900130.png" alt="image-20231126153900130" style="zoom:67%;" />

#### 修改业务方法

- 不需要编写操作缓存相关方法了

```java
@Cacheable(value = "category",key = "#root.methodName.substring(3)")
public Map<String, List<Catelog2Vo>> getCatelogJson() {
    //查询所有
    List<CategoryEntity> selectList = baseMapper.selectList(new QueryWrapper<>(null));
    //1.查出所有一级分类
    List<CategoryEntity> levelFirst = getParentId(selectList, 0l);
    //封装数据
    Map<String, List<Catelog2Vo>> map = 
        levelFirst.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
        //2.查询每个一级分类的所有二级分类
        List<CategoryEntity> categoryEntities = getParentId(selectList, v.getParentCid());
        List<Catelog2Vo> catelog2VoList = null;
        if (categoryEntities != null) {
            catelog2VoList = categoryEntities.stream().map(level2 -> {
                Catelog2Vo catelog2Vo = new Catelog2Vo
                        (v.getCatId().toString(), null, level2.getCatId().toString(), level2.getName());
                //3.查询当前二级分类的三级分类
                List<CategoryEntity> catelog3List = getParentId(selectList, level2.getParentCid());
                if (catelog3List != null) {
                    //封装成指定格式
                    List<Catelog2Vo.Catelog3Vo> catelog3VoList = catelog3List.stream().map(level3 -> {
                        Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo
                                (level2.getCatId().toString(),
                                        level3.getCatId().toString(), level3.getName());
                        return catelog3Vo;
                    }).collect(Collectors.toList());
                    catelog2Vo.setCatalog3List(catelog3VoList);
                }
                return catelog2Vo;
            }).collect(Collectors.toList());
        }
        return catelog2VoList;
    }));
    return map;
}
```

- 访问结果

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126154524054.png" alt="image-20231126154524054" style="zoom:67%;" /><img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126154533286.png" alt="image-20231126154533286" style="zoom:67%;" />

- 修改数据时删除一级菜单和三级分类的缓存

  - 使用@Caching组合两个删除操作

    ```java
    @Caching(evict ={
            @CacheEvict(value = "category",key="'LevelFirst'"),
            @CacheEvict(value = "category",key="'CatelogJson'")
    } )
    ```

  - 删除一个缓存分区中所有缓存`@CacheEvict(value = "category",allEntries = true)`

    - 业务规定：存储同一个类型的数据，都可以指定成同一个分区，并且开启前缀【采用默认的分区名作为前缀】

    <img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20231126161055059.png" alt="image-20231126161055059" style="zoom: 80%;" />.

#### 不足

- springcache**默认是无加锁**的，无法解决缓存击穿问题
  - 可以在@Cacheable中指定`sync=true`参数给当前服务**加本地锁**
  - 但是**只在查询**的时候会加锁，获取锁之后会**再判断缓存**中是否有数据【双检】
- 总结
  - 常规数据（读多写少、即时性、一致性要求不高的）可以用spring cache，写操作有过期时间就足够了，容忍读取脏数据
  - 特殊数据就得特殊设计

## 高级篇完结撒花

<img src="C:\Users\LPW\AppData\Roaming\Typora\typora-user-images\image-20240128175301882.png" alt="image-20240128175301882"  />

- 总结
  - 高并发有三宝：缓存、异步、队排好
  - 订单下单和库存锁定中采用延时队列+最终一致性的方案实现分布式事务
    - 订单服务和库存服务各自有自己的延时队列，其中订单服务的延时队列用于超时关单，库存服务的用于超时自动结束库存
    - 库存服务的超时时间要设计的比订单服务的长【否则库存服务先去检查目标商品的状态，此时还未取消，库存就无法解锁】
    - 考虑到网络延时问题，有可能库存服务先收到消息，订单服务关单时应该再主动调用一次解锁逻辑
