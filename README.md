# Fitbit Challenge
[Link to the challenge](https://github.com/arora-aman/fitbit-challenge/tree/master/challenge)
![Demo](https://github.com/arora-aman/fitbit-challenge/blob/master/demo.gif)

### Design Pattern
**MVVM.** - Model View ViewModel
- Model: `Row` - Stores important information about the data received from the socket.
- View: `MainActivity` - Contains only logic responsible for displaying contents on the screen.
- ViewModel: `ColorsViewModel` - Responsible for initiating the connection to the socket and 
                 parsing contents into usuable Model objects.

### Architecture Components
The app uses the new architecture lifecycle components

### Custom Host and Port Values
The AsyncTask takes the host and port values as input, which are provided to it by the viewmodel.
ViewModel receives those values from the activity and in case of missing host value viewmodel uses a default set of values.
For providing custom values in-app - input can be taken from the user and then passed to the viewmodel from the activity. 

### Key Classes/ Interfaces

- `ClientSocketWrapper` to wrap around some basic `Socket` functionality to ease unit-testing.
- `SocketAsyncTask` that opens a connection to the Socket, then continually reads 8 bit commands and followed by
  arguments as specified by enum `Command`.
- `OnBytesCallback` which invokes `onBytesRead()` once a command and all specified arguments are read from the input stream.
- `Row` contains the command id for each set of instructions read, arguments in the set and then final RGB values after the command is executed.

### Important Points to Note

- ViewModel doesn't contain/ has access to the Context of any View.
- For observing LiveData, context of a view is passed to the Observer interface. When the Activity gets destroyed the LiveData object looses access to the context preventing any leaks. 
>LiveData keeps a strong reference to the observer and the owner as long as the given LifecycleOwner is not destroyed. When it is destroyed, LiveData removes references to the observer & the owner.
- Since the async task is hosted inside the ViewModel which persists as a single object even if the Activity gets destroyed and then re-created, no memory leaks can happen.
- There are two ways of posting updates to LiveData - `postValue()` and `sendValue()`. If the the `mMainThreadHandler` is not set `postValue()` will be used and a couple of updates might appear to happen all of a sudden.
- Since the applicaition codebase isn't extensive and classes didn't involve a lot of dependencies. Dagger wasn't used. 
- Based on the background color, text color is set to either white or black for ease of readability.

### Unit Testing
- Libraries that can be used = Mockito, AssertJ, Robolectric.
- Important methods/classes that can be used: `assertThat()`, `verify()`, `ArgumentCaptor`.
- Using `ClientSocketWrapper` the InputStream and Socket can be mocked. 
- Bytes on the mocked InputStream can be set, and the values with which the callback is called can be `verified`.
- `ColorUtilsWrapper` can be created similar to `ClientSocketWrapper` to wrap some ColorUtils functionality for easier unit testing of the ViewModel.
- Wrapper around the constructor for AsyncTask can make `capturing` and then testing the `BytesProcessedCallback` easy.
- Mock handler can be set as the MainThreadHandler.
- Mocked data can be used on the BytesProcessedCallback and expected output can be `asserted`.
- All the logic for displaying content in the UI is in ViewModel which can completely be tested.
