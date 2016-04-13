# TouchEven

# 说明 #
 
本项目是基于 *[Android 编程下 Touch 事件的分发和消费机制](http://www.cnblogs.com/sunzn/archive/2013/05/10/3064129.html#!comments)* 完成， 根据自己的实验完善原博文的相关总结。

## 程序说明 ##

程序界面如下：

![](http://i.imgur.com/YSum0Kx.png)

MainActivity：表示整个项目的Activity；
ParentLayout：表示下面的蓝色区域；
ChildLayout：表示下面的红色区域；

上面的下拉选择，用于控制Activity、ParentLayout、ChildLayout 的  public boolean dispatchTouchEvent(MotionEvent ev)、  public boolean onInterceptTouchEvent(MotionEvent ev)、 public boolean onTouchEvent(MotionEvent ev)三个函数的返回值。  

通过查看控制台的Log信息用以判断Touch事件的传递过程。


### 一、Touch 事件分析 ###

▐ 事件分发：public boolean dispatchTouchEvent(MotionEvent ev)

Touch 事件发生时 Activity 的 dispatchTouchEvent(MotionEvent ev) 方法会以**隧道方式**（从根元素依次往下传递直到最内层子元素或在中间某一元素中由于某一条件停止传递）将事件传递给最外层 View 的 dispatchTouchEvent(MotionEvent ev) 方法，并由该 View 的 dispatchTouchEvent(MotionEvent ev) 方法对事件进行分发。dispatchTouchEvent 的事件分发逻辑如下：

如果 return true，事件会分发给当前 View 并由 dispatchTouchEvent 方法进行消费，同时事件会停止向下传递；
如果 return false，事件分发分为三种情况：（也都是停止向下传递）
	
- 如果当前 View 获取的事件直接来自 Activity，则会将事件返回给 Activity 的 onTouchEvent 进行消费；
- 如果当前 View 获取的事件来自外层父控件，则会将事件返回给父 View 的  onTouchEvent 进行消费；
- 如果当前为Activity，由 dispatchTouchEvent 方法进行消费，同时事件会停止向下传递。

如果返回系统默认的 super.dispatchTouchEvent(ev)，事件会自动的分发给当前 View 的 onInterceptTouchEvent 方法。

▐ 事件拦截：public boolean onInterceptTouchEvent(MotionEvent ev) 

在外层 View 的 dispatchTouchEvent(MotionEvent ev) 方法返回系统默认的 super.dispatchTouchEvent(ev) 情况下，事件会自动的分发给当前 View 的 onInterceptTouchEvent 方法。onInterceptTouchEvent 的事件拦截逻辑如下：

- 如果 onInterceptTouchEvent 返回 true，则表示将事件进行拦截，并将拦截到的事件交由当前 View 的 onTouchEvent 进行处理；
- 如果 onInterceptTouchEvent 返回 false，则表示将事件放行，当前 View 上的事件会被传递到子 View 上，再由子 View 的 dispatchTouchEvent 来开始这个事件的分发；
- 如果 onInterceptTouchEvent 返回 super.onInterceptTouchEvent(ev)，默认是false，则表示将事件放行，当前 View 上的事件会被传递到子 View 上，再由子 View 的 dispatchTouchEvent 来开始这个事件的分发；

▐ 事件响应：public boolean onTouchEvent(MotionEvent ev)

在 dispatchTouchEvent 返回 super.dispatchTouchEvent(ev) 并且 onInterceptTouchEvent 返回 true 或返回 super.onInterceptTouchEvent(ev) 的情况下 onTouchEvent 会被调用。onTouchEvent 的事件响应逻辑如下：

- 如果事件传递到当前 View 的 onTouchEvent 方法，而该方法返回了 false，那么这个事件会从当前 View 向上传递，并且都是由上层 View 的 onTouchEvent 来接收，如果传递到上面的 onTouchEvent 也返回 false，则会传递到Activity级别，由其响应。
- 如果返回了 true 则会接收并消费该事件（onClick便不会执行了）。
- 如果返回 super.onTouchEvent(ev) 默认处理事件的逻辑和返回 false 时相同。

到这里，与 Touch 事件相关的三个方法就分析完毕了。下面的内容会通过各种不同的的测试案例来验证上文中三个方法对事件的处理逻辑。

### 二、案例分析 ###

### 第一种情况： ###

拦截条件全部为系统默认的 super


| 控件名称        | dispatchTouchEvent 返回值           | onInterceptTouchEvent 返回值  | onTouchEvent 返回值  |
|  ------------ |:-------------:| -----:| -----:|
| MainActivity      | super.dispatchTouchEvent(ev) | 无  | super.onTouchEvent(ev) |
| ParentLayout      | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |   super.onTouchEvent(ev) |
| ChildLayout | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |    super.onTouchEvent(ev) |

**点击事件：**点击 ParentLayout

**运行结果**

![](http://i.imgur.com/q21AWvU.png)

**结果分析**

- 点击 ParentLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给当前 View （ParentLayout）的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 super，事件理论应该继续向子View传递，但是当前View已经是最外层的View，所以 ParentLayout 默认会拦截事件，并将拦截到的事件交由 ParentLayout 的 onTouchEvent 进行处理
- ParentLayout 控件的 onTouchEvent 返回 super，表明对事件没有做任何处理直接将事件返回给上级控件 ，由于 ParentLayout 获取的事件直接来自 MainActivity，所以 ParentLayout 控件的 onTouchEvent 会将事件以冒泡方式直接返回给 MainActivity 的 onTouchEvent 进行消费
- 后续的事件则会跳过 ParentLayout 直接由 MainActivity 的 onTouchEvent 进行消费，最后直接由 MainActivity 来响应手指抬起事件。

**点击事件：**点击 ChildLayout

**运行结果**

![](http://i.imgur.com/SfuEFFk.png)

**结果分析**

- 点击 ChildLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给 ParentLayout 的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 super，事件会被放行并传递到子控件 ChildLayout 的 dispatchTouchEvent 方法
- ChildLayout 的 dispatchTouchEvent 返回 super，表示对事件进行分发并向下传递给 ChildLayout 控件的 onInterceptTouchEvent 方法
- ChildLayout 的 onInterceptTouchEvent 返回 super ，事件理论应该继续向子View传递，但是当前View已经是最外层的View，所以 ChildLayout 的 onTouchEvent 进行处理
- ChildLayout 的 onTouchEvent 返回 super ，表示对事件没有做任何处理直接将事件返回给上级控件，由于 ChildLayout 获取的事件直接来自 ParentLayout，所以 ChildLayout 控件的 onTouchEvent 会将事件以冒泡方式直接返回给 ParentLayout 的 onTouchEvent 进行消费
- 而 ParentLayout 的 onTouchEvent 也返回了 super ，同样 ParentLayout 的 onTouchEvent 也会将事件返回给上级控件，而 ParentLayout 获取的事件直接来自 MainActivity，所以 ParentLayout 控件的 onTouchEvent 会将事件以冒泡方式直接返回给 MainActivity 的 onTouchEvent 进行消费
- 后续的事件则会跳过 ParentLayout 和 ChildLayout 直接由 MainActivity 的 onTouchEvent 消费来自 MainActivity 自身分发的事件。

### 第二种情况： ###

拦截条件为：MainActivity的 dispatchTouchEvent 返回值为false or true


| 控件名称        | dispatchTouchEvent 返回值           | onInterceptTouchEvent 返回值  | onTouchEvent 返回值  |
|  ------------ |:-------------:| -----:| -----:|
| MainActivity      | false | 无  | super.onTouchEvent(ev) |
| ParentLayout      | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |   super.onTouchEvent(ev) |
| ChildLayout | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |    super.onTouchEvent(ev) |

**点击事件：**点击 ParentLayout

**运行结果**

![](http://i.imgur.com/BIqjlSQ.png)

**结果分析**

- 点击 ParentLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 false，表明MainActivity 不向其子View分发事件，则 ParentLayout 是不会接收到事件的传递，而由于 MainActivity 是最外层的View，其 onTouchEvent 不会响应手指抬起事件。


**点击事件：**点击 ChildLayout

**运行结果**

![](http://i.imgur.com/BIqjlSQ.png)（结果和上面一致，图片不再新截图）

**结果分析**

- 点击 ParentLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 false，表明MainActivity不向其子View分发事件，则 ParentLayout 是不会接收到事件的传递，而由于 MainActivity 是最外层的View，其 onTouchEvent 不会响应手指抬起事件。

备注：Activity 的 dispatchTouchEvent 这个方法是从父view传递到子view的，负责事件的分发，不管是return true或者false它都不会继续分发下去，而是被自己的 dispatchTouchEvent 消费掉
此种情况下，你将无法再选择其他选项，原因嘛，应该懂的。解决方法，杀死进程。

### 第三种情况： ###

拦截条件为：ParentLayout dispatchTouchEvent 返回值为 false


| 控件名称        | dispatchTouchEvent 返回值           | onInterceptTouchEvent 返回值  | onTouchEvent 返回值  |
|  ------------ |:-------------:| -----:| -----:|
| MainActivity      | super.dispatchTouchEvent(ev) | 无  | super.onTouchEvent(ev) |
| ParentLayout      | false      | super.onInterceptTouchEvent(ev) |   super.onTouchEvent(ev) |
| ChildLayout | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |    super.onTouchEvent(ev) |

**点击事件：**点击 ParentLayout

**运行结果**

![](http://i.imgur.com/2VMOayi.png)

**结果分析**

- 点击 ParentLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 的 dispatchTouchEvent 返回 false，表示对获取到的事件停止向下传递，同时也不对该事件进行消费
- 由于 ParentLayout 获取的事件直接来自 MainActivity ，则会将事件返回给 MainActivity 的 onTouchEvent 进行消费，最后直接由 MainActivity 来响应手指移动和抬起事件。


**点击事件：**点击 ChildLayout

**运行结果**

![](http://i.imgur.com/2VMOayi.png)（结果和上面一致，图片不再新截图）

**结果分析**

- 点击 ParentLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 的 dispatchTouchEvent 返回 false，表示对获取到的事件停止向下传递（ ChildLayout 也不回得到相应），同时也不对该事件进行消费
- 由于 ParentLayout 获取的事件直接来自 MainActivity ，则会将事件返回给 MainActivity 的 onTouchEvent 进行消费，最后直接由 MainActivity 来响应手指移动和抬起事件。

### 第四种情况： ###

拦截条件为：ParentLayout dispatchTouchEvent 返回值为 true


| 控件名称        | dispatchTouchEvent 返回值           | onInterceptTouchEvent 返回值  | onTouchEvent 返回值  |
|  ------------ |:-------------:| -----:| -----:|
| MainActivity      | super.dispatchTouchEvent(ev) | 无  | super.onTouchEvent(ev) |
| ParentLayout      | true      | super.onInterceptTouchEvent(ev) |   super.onTouchEvent(ev) |
| ChildLayout | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |    super.onTouchEvent(ev) |

**点击事件：**点击 ParentLayout

**运行结果**

![](http://i.imgur.com/p7RknqK.png)

**结果分析**

- 点击 ParentLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 的 dispatchTouchEvent 返回 true，表示对获取到的事件停止向下传递，事件分发到 ParentLayout 控件并由该控件的 dispatchTouchEvent 进行消费
- MainActivity 不断的分发事件到 ParentLayout 控件的 dispatchTouchEvent，而 ParentLayout 控件的 dispatchTouchEvent 也不断的将获取到的事件进行消费。


**点击事件：**点击 ChildLayout

**运行结果**

![](http://i.imgur.com/p7RknqK.png)（结果和上面一致，图片不再新截图）

**结果分析**

- 点击 ParentLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 的 dispatchTouchEvent 返回 true，表示对获取到的事件停止向下传递，事件分发到 ParentLayout 控件并由该控件的 dispatchTouchEvent 进行消费
- MainActivity 不断的分发事件到 ParentLayout 控件的 dispatchTouchEvent，而 ParentLayout 控件的 dispatchTouchEvent 也不断的将获取到的事件进行消费。

备注： 在这里我们可以看出，Activity 和 ViewGroup 在处理 dispatchTouchEvent 的不同

### 第五种情况： ###

拦截条件为：ParentLayout onInterceptTouchEvent 返回值为 true


| 控件名称        | dispatchTouchEvent 返回值           | onInterceptTouchEvent 返回值  | onTouchEvent 返回值  |
|  ------------ |:-------------:| -----:| -----:|
| MainActivity      | super.dispatchTouchEvent(ev) | 无  | super.onTouchEvent(ev) |
| ParentLayout      | super.dispatchTouchEvent(ev)      | true |   super.onTouchEvent(ev) |
| ChildLayout | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |    super.onTouchEvent(ev) |

**点击事件：**点击 ParentLayout

**运行结果**

![](http://i.imgur.com/q21AWvU.png)

**结果分析**

- 点击 ParentLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给 ParentLayout 的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 true，表示对所获取到的事件进行拦截并将事件传递给 ParentLayout 控件的 onTouchEvent 进行处理
- ParentLayout 控件的 onTouchEvent 返回 super ，表示对事件没有做任何处理直接将事件返回给上级控件
- 由于 ParentLayout 获取的事件直接来自 MainActivity，所以 ParentLayout 控件的 onTouchEvent 会将事件以冒泡方式直接返回给 MainActivity 的 onTouchEvent 进行消费
- 后续的事件则会跳过 ParentLayout ，直接由 MainActivity 的 onTouchEvent 消费来自 MainActivity 自身分发的事件。


**点击事件：**点击 ChildLayout

**运行结果**

![](http://i.imgur.com/q21AWvU.png)（结果和上面一致，图片不再新截图）

**结果分析**

- 点击 ParentLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给 ParentLayout 的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 true，表示对所获取到的事件进行拦截并将事件传递给 ParentLayout 控件的 onTouchEvent 进行处理。（此时 ChildLayout 将收不到事件）
- ParentLayout 控件的 onTouchEvent 返回 super 表示对事件没有做任何处理直接将事件返回给上级控件
- 由于 ParentLayout 获取的事件直接来自 MainActivity，所以 ParentLayout 控件的 onTouchEvent 会将事件以冒泡方式直接返回给 MainActivity 的 onTouchEvent 进行消费
- 后续的事件则会跳过 ParentLayout ，直接由 MainActivity 的 onTouchEvent 消费来自 MainActivity 自身分发的事件。

### 第六种情况： ###

拦截条件为：ChildLayout dispatchTouchEvent 返回值为 true


| 控件名称        | dispatchTouchEvent 返回值           | onInterceptTouchEvent 返回值  | onTouchEvent 返回值  |
|  ------------ |:-------------:| -----:| -----:|
| MainActivity      | super.dispatchTouchEvent(ev) | 无  | super.onTouchEvent(ev) |
| ParentLayout      | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |   super.onTouchEvent(ev) |
| ChildLayout | true      | super.onInterceptTouchEvent(ev) |    super.onTouchEvent(ev) |

**点击事件：**点击 ParentLayout

**运行结果**

![](http://i.imgur.com/q21AWvU.png)

**结果分析**

- 点击ParentLayout后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给当前 View （ParentLayout）的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 super，事件理论应该继续向子View传递，但是当前View已经是最外层的View，所以 ParentLayout 默认会拦截事件，并将拦截到的事件交由 ParentLayout 的 onTouchEvent 进行处理
- ParentLayout 控件的 onTouchEvent 返回 super，表明对事件没有做任何处理直接将事件返回给上级控件 ，由于 ParentLayout 获取的事件直接来自 MainActivity，所以 ParentLayout 控件的 onTouchEvent 会将事件以冒泡方式直接返回给 MainActivity 的 onTouchEvent 进行消费
- 后续的事件则会跳过 ParentLayout 直接由 MainActivity 的 onTouchEvent 进行消费，最后直接由 MainActivity 来响应手指抬起事件。


**点击事件：**点击 ChildLayout

**运行结果**

![](http://i.imgur.com/X8Ee7F3.png)

**结果分析**

- 点击 ParentLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给 ParentLayout 的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 super，事件会被放行并传递到子控件 ChildLayout 的 dispatchTouchEvent 方法
- ChildLayout 的 dispatchTouchEvent 返回 true，表示事件被分发到 ChildLayout 控件并由该控件的 dispatchTouchEvent 方法消费
- 后续的事件也会不断的重复上面的逻辑最终被 ChildLayout 的 dispatchTouchEvent 消费。

### 第七种情况： ###

拦截条件为：ChildLayout dispatchTouchEvent 返回值为 false


| 控件名称        | dispatchTouchEvent 返回值           | onInterceptTouchEvent 返回值  | onTouchEvent 返回值  |
|  ------------ |:-------------:| -----:| -----:|
| MainActivity      | super.dispatchTouchEvent(ev) | 无  | super.onTouchEvent(ev) |
| ParentLayout      | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |   super.onTouchEvent(ev) |
| ChildLayout | false      | super.onInterceptTouchEvent(ev) |    super.onTouchEvent(ev) |

**点击事件：**点击 ParentLayout

**运行结果**

![](http://i.imgur.com/q21AWvU.png)

**结果分析**

- 点击ParentLayout后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给当前 View （ParentLayout）的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 super，事件理论应该继续向子View传递，但是当前View已经是最外层的View，所以 ParentLayout 默认会拦截事件，并将拦截到的事件交由 ParentLayout 的 onTouchEvent 进行处理
- ParentLayout 控件的 onTouchEvent 返回 super，表明对事件没有做任何处理直接将事件返回给上级控件 ，由于 ParentLayout 获取的事件直接来自 MainActivity，所以 ParentLayout 控件的 onTouchEvent 会将事件以冒泡方式直接返回给 MainActivity 的 onTouchEvent 进行消费
- 后续的事件则会跳过 ParentLayout 直接由 MainActivity 的 onTouchEvent 进行消费，最后直接由 MainActivity 来响应手指抬起事件。


**点击事件：**点击 ChildLayout

**运行结果**

![](http://i.imgur.com/qyWBs31.png)

**结果分析**

- 点击 ParentLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给 ParentLayout 的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 super，事件会被放行并传递到子控件 ChildLayout 的 dispatchTouchEvent 方法
- ChildLayout 的 dispatchTouchEvent 返回 false，表示对获取到的事件停止向下传递，会将事件返回给 ParentLayout 的 onTouchEvent 进行消费
- ParentLayout 控件的 onTouchEvent 返回但是 super，表明对事件没有做任何处理直接将事件返回给上级控件 ，由于 ParentLayout 获取的事件直接来自 MainActivity，所以 ParentLayout 控件的 onTouchEvent 会将事件以冒泡方式直接返回给 MainActivity 的 onTouchEvent 进行消费
- 后续的事件则会跳过 ParentLayout 直接由 MainActivity 的 onTouchEvent 进行消费，最后直接由 MainActivity 来响应手指抬起事件。

### 第八种情况： ###

拦截条件为：ChildLayout onTouchEvent 返回值为 true


| 控件名称        | dispatchTouchEvent 返回值           | onInterceptTouchEvent 返回值  | onTouchEvent 返回值  |
|  ------------ |:-------------:| -----:| -----:|
| MainActivity      | super.dispatchTouchEvent(ev) | 无  | super.onTouchEvent(ev) |
| ParentLayout      | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |   super.onTouchEvent(ev) |
| ChildLayout | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |    true |

**点击事件：**点击 ParentLayout

**运行结果**

![](http://i.imgur.com/q21AWvU.png)

**结果分析**

- 点击ParentLayout后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给当前 View （ParentLayout）的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 super，事件理论应该继续向子View传递，但是当前View已经是最外层的View，所以 ParentLayout 默认会拦截事件，并将拦截到的事件交由 ParentLayout 的 onTouchEvent 进行处理
- ParentLayout 控件的 onTouchEvent 返回 super，表明对事件没有做任何处理直接将事件返回给上级控件 ，由于 ParentLayout 获取的事件直接来自 MainActivity，所以 ParentLayout 控件的 onTouchEvent 会将事件以冒泡方式直接返回给 MainActivity 的 onTouchEvent 进行消费
- 后续的事件则会跳过 ParentLayout 直接由 MainActivity 的 onTouchEvent 进行消费，最后直接由 MainActivity 来响应手指抬起事件。


**点击事件：**点击 ChildLayout

**运行结果**

![](http://i.imgur.com/S1dVKRy.png)

**结果分析**

- 点击 ParentLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给 ParentLayout 的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 super，事件会被放行并传递到子控件 ChildLayout 的 dispatchTouchEvent 方法
- ChildLayout 的 dispatchTouchEvent 返回 super，表示对事件进行分发并向下传递给 ChildLayout 控件的 onInterceptTouchEvent 方法
- ChildLayout 的 onInterceptTouchEvent 方法返回 super ,事件理论应该继续向子View传递，但是当前View已经是最外层的View，所以 ChildLayout 的 onTouchEvent 进行处理
- ChildLayout 的 onTouchEvent 返回 true ,表明 ChildLayout 自己消费掉了事件
- MainActivity 不断的分发事件到 ChildLayout 控件的 onTouchEvent，而 ChildLayout 控件的 onTouchEvent 也不断的将获取到的事件进行消费。

### 第九种情况： ###

拦截条件为：ChildLayout onTouchEvent 返回值为 false


| 控件名称        | dispatchTouchEvent 返回值           | onInterceptTouchEvent 返回值  | onTouchEvent 返回值  |
|  ------------ |:-------------:| -----:| -----:|
| MainActivity      | super.dispatchTouchEvent(ev) | 无  | super.onTouchEvent(ev) |
| ParentLayout      | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |   super.onTouchEvent(ev) |
| ChildLayout | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |    false |

**点击事件：**点击 ParentLayout

**运行结果**

![](http://i.imgur.com/q21AWvU.png)

**结果分析**

- 点击ParentLayout后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给当前 View （ParentLayout）的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 super，事件理论应该继续向子View传递，但是当前View已经是最外层的View，所以 ParentLayout 默认会拦截事件，并将拦截到的事件交由 ParentLayout 的 onTouchEvent 进行处理
- ParentLayout 控件的 onTouchEvent 返回 super，表明对事件没有做任何处理直接将事件返回给上级控件 ，由于 ParentLayout 获取的事件直接来自 MainActivity，所以 ParentLayout 控件的 onTouchEvent 会将事件以冒泡方式直接返回给 MainActivity 的 onTouchEvent 进行消费
- 后续的事件则会跳过 ParentLayout 直接由 MainActivity 的 onTouchEvent 进行消费，最后直接由 MainActivity 来响应手指抬起事件。


**点击事件：**点击 ChildLayout

**运行结果**

![](http://i.imgur.com/qkqtQbQ.png)

**结果分析**

- 点击 ParentLayout 后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给 ParentLayout 的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 super，事件会被放行并传递到子控件 ChildLayout 的 dispatchTouchEvent 方法
- ChildLayout 的 dispatchTouchEvent 返回 super，表示对事件进行分发并向下传递给 ChildLayout 控件的 onInterceptTouchEvent 方法
- ChildLayout 的 onInterceptTouchEvent 方法返回 super ,事件理论应该继续向子View传递，但是当前View已经是最外层的View，所以 ChildLayout 的 onTouchEvent 进行处理
- ChildLayout 的 onTouchEvent 返回 false ，表明 ChildLayout 自己不消费事件，那么这个事件会从当前 View 向上传递，并且都是由 ParentLayout 的 onTouchEvent 来接收
- 而 ParentLayout 的 onTouchEvent 返回了 super ，同样 ParentLayout 的 onTouchEvent 也会将事件返回给上级控件，而 ParentLayout 获取的事件直接来自 MainActivity，所以 ParentLayout 控件的 onTouchEvent 会将事件以冒泡方式直接返回给 MainActivity 的 onTouchEvent 进行消费
- 后续的事件则会跳过 ParentLayout 和 ChildLayout 直接由 MainActivity 的 onTouchEvent 消费来自 MainActivity 自身分发的事件。


### 第十种情况： ###

拦截条件为：ParentLayout onTouchEvent 返回值为 true； ParentLayout onInterceptTouchEvent 返回值为 true


| 控件名称        | dispatchTouchEvent 返回值           | onInterceptTouchEvent 返回值  | onTouchEvent 返回值  |
|  ------------ |:-------------:| -----:| -----:|
| MainActivity      | super.dispatchTouchEvent(ev) | 无  | super.onTouchEvent(ev) |
| ParentLayout      | super.dispatchTouchEvent(ev)      | true |   true |
| ChildLayout | super.dispatchTouchEvent(ev)      | super.onInterceptTouchEvent(ev) |    super.onTouchEvent(ev) |

**点击事件：**点击 ParentLayout

**运行结果**

![](http://i.imgur.com/SH90QBR.png)

**结果分析**

- 点击ParentLayout后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给当前 View （ParentLayout）的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 true，默认会拦截事件，并将拦截到的事件交由 ParentLayout 的 onTouchEvent 进行处理
- ParentLayout 控件的 onTouchEvent 返回 true，表明 ParentLayout 的 onTouchEvent 进行消费
- MainActivity 不断的分发事件到 ParentLayout 控件的 onTouchEvent，而 ChildLayout 控件的 onTouchEvent 也不断的将获取到的事件进行消费。


**点击事件：**点击 ChildLayout

**运行结果**

![](http://i.imgur.com/SH90QBR.png)（结果和上面一致，图片不再新截图）

**结果分析**

- 点击ParentLayout后，首先由 MainActivity 的 dispatchTouchEvent 拦截，其返回 super，默认分发给 ParentLayout 控件的 dispatchTouchEvent
- ParentLayout 控件的 dispatchTouchEvent 返回 super，则事件会自动的分发给当前 View （ParentLayout）的 onInterceptTouchEvent 方法
- ParentLayout 控件的 onInterceptTouchEvent 返回 true，默认会拦截事件（事件不会传给 ChildLayout），并将拦截到的事件交由 ParentLayout 的 onTouchEvent 进行处理
- ParentLayout 控件的 onTouchEvent 返回 true，表明 ParentLayout 的 onTouchEvent 进行消费
- MainActivity 不断的分发事件到 ParentLayout 控件的 onTouchEvent，而 ChildLayout 控件的 onTouchEvent 也不断的将获取到的事件进行消费。

## 补充 ##

关于Android事件传递的源码分析可以参考以下博客：

[Android触摸屏事件派发机制详解与源码分析一(View篇)](http://blog.csdn.net/yanbober/article/details/45887547)

[Android触摸屏事件派发机制详解与源码分析二(ViewGroup篇)](http://blog.csdn.net/yanbober/article/details/45912661)

[Android触摸屏事件派发机制详解与源码分析三(Activity篇)](http://blog.csdn.net/yanbober/article/details/45932123)